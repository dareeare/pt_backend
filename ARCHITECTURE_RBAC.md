# Role-Based Access Control (RBAC) Architecture

## Overview
The application uses a distributed Role-Based Access Control system split between `AuthService` (Identity Provider) and `UserService` (Resource Server). The system ensures that:
1.  Users are authenticated centrally.
2.  Roles are enforced at both the Gateway/Frontend level and the Backend Service level.
3.  User profiles are synchronized across services.

## Services Roles

### AuthService (Port 8080)
*   **Responsibility**: Manages Credentials (phone, password) and Roles (`ROLE_DOCTOR`, `ROLE_OPERATOR`, `ROLE_PATIENT`).
*   **Database**: Stores `users`, `roles`, and `refresh_tokens`.
*   **Key Function**: Issues JWT Access Tokens containing the user's Role and Phone number.
*   **Registration Flow**:
    *   Public (`/signup`): Always creates `ROLE_PATIENT`.
    *   Staff (`/signup/staff`): Protected by Secret Key. Creates `ROLE_DOCTOR` or `ROLE_OPERATOR`.
    *   **Synchronization**: Upon successful registration, `AuthService` calls `UserService` API to create the corresponding detailed profile.

### UserService (Port 8081)
*   **Responsibility**: Manages Business Logic (Appointments, Chat, Profiles).
*   **Database**: Stores `doctors`, `operators`, `patients`, `messages`, etc.
*   **Security**: Validates JWT Tokens issued by `AuthService`.
*   **Endpoints**:
    *   Protected by `JwtAuthenticationFilter`.
    *   Specific actions (e.g., broadcasting messages) are restricted using `@PreAuthorize("hasRole('OPERATOR')")`.

## Authentication Flow

1.  **Login**: User sends `phone` + `password` to `AuthService`.
2.  **Token**: `AuthService` returns a **JWT Access Token**.
    *   Payload: `sub: +7999...`, `roles: [ROLE_OPERATOR]`.
3.  **Access**: Frontend sends this Token in the `Authorization: Bearer <token>` header to `UserService`.
4.  **Validation**: `UserService` validates the signature using the shared Secret Key.

## User Synchronization Logic

To ensure data consistency, when a user is created in `AuthService`:
1.  A transaction starts.
2.  User is saved in `AuthService` DB.
3.  `UserSyncService` generates a temporary token.
4.  `UserSyncService` sends a POST request to `UserService` (`/api/operators`, `/api/doctors`, etc.) with profile data.
5.  If `UserService` fails (e.g., invalid data or server down), the transaction in `AuthService` rolls back.

## Chat System

*   **Technology**: WebSocket (STOMP) over SockJS.
*   **Security**:
    *   Connection is rejected if no valid JWT is provided in the handshake headers.
    *   `JwtUtil` extracts the user identity from the token.
*   **Identity Resolution**:
    *   When a message is sent, the system resolves the sender's Phone Number from the Principal.
    *   It then queries the appropriate repository (Operator/Doctor/Patient) to find the sender's Real Name (First + Last Name) to display in the chat.

## Frontend Security (Angular)

*   **Session Storage**: Used instead of LocalStorage to allow multiple users on different tabs.
*   **Guards**: `AuthGuard` checks the JWT role claim before allowing navigation to `/operator`, `/doctor`, etc.
*   **Interceptors**: Automatically attaches the JWT to every HTTP request.

