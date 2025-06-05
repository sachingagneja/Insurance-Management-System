# Project Documentation

## 1. Project Overview

*   **Project Title:** Digital Insurance Management System
*   **Project Goal:** A Spring Boot application for managing digital insurance policies and user data.
*   **Technologies Used:**
    *   **Backend:**
        *   Spring Boot
        *   Java 17
        *   Spring Data JPA
        *   Spring Web
        *   Spring Security
        *   Lombok
        *   jsonwebtoken (jjwt)
    *   **Frontend:**
        *   Vue.js 3
        *   TypeScript
        *   Vite
        *   Vue Router
        *   Axios
        *   Tailwind CSS
        *   Bootstrap
    *   **Database:**
        *   MySQL (configured via `docker-compose.yml` and `application.properties`)
    *   **Testing:**
        *   JUnit (implied by `spring-boot-starter-test`)
        *   Mockito (based on test file analysis, e.g., `UserPolicyPurchaseServiceTest.java`, `SupportTicketControllerTest.java`)
        *   H2 (for testing, as per `pom.xml`)
    *   **Build Tool:**
        *   Maven (for backend)
        *   npm/vite (for frontend)

## 2. System Architecture

*   **Backend (Spring Boot):**
    *   **REST API Endpoints:**
        *   **Auth Controller (`/auth`):**
            *   `POST /register`: Registers a new user.
            *   `POST /login`: Logs in an existing user.
        *   **Policy Controller (`/policies`):**
            *   `GET /`: Retrieves all available insurance policies.
            *   `GET /{id}`: Retrieves a specific policy by its ID.
            *   `POST /create` (ADMIN): Creates a new insurance policy.
            *   `PUT /update/{id}` (ADMIN): Updates an existing policy.
            *   `DELETE /delete/{id}` (ADMIN): Deletes a policy.
        *   **User Policy Purchase Controller (`/user/policy`):**
            *   `POST /{policyId}/purchase`: Allows a user to purchase a policy.
            *   `GET /`: Retrieves all policies purchased by the current user.
            *   `PUT /`: Updates the status of a user's policy (e.g., to ACTIVE, EXPIRED).
        *   **Policy Renewal Controller:**
            *   `POST /policy/{policyId}/renew`: Renews an existing policy for the user.
            *   `GET /user/policies/renewable`: Retrieves a list of policies that are eligible for renewal for the current user.
        *   **Claim Management Controller:**
            *   `POST /claim`: Submits a new claim for a user's policy.
            *   `GET /user/claim`: Retrieves all claims submitted by all users (ADMIN specific, based on typical design, though controller method name is generic).
            *   `GET /user/claimById`: Retrieves claims for the currently authenticated user.
            *   `PUT /claim/{claimId}/status` (ADMIN): Updates the status of a claim (e.g., PENDING, APPROVED, REJECTED).
            *   `DELETE /claim/{id}` (ADMIN): Deletes a claim.
        *   **Support Ticket Controller (`/support`):**
            *   `POST /`: Creates a new support ticket.
            *   `GET /user`: Retrieves all support tickets for the currently authenticated user.
            *   `PUT /{ticketId}` (ADMIN): Updates a support ticket (e.g., adds a response, changes status).
            *   `GET /` (ADMIN): Retrieves all support tickets in the system.
            *   `DELETE /{ticketId}` (ADMIN): Deletes a support ticket.
    *   **Data Models (Entities):**
        ### User

        | Field    | Type    | Description             |
        |----------|---------|-------------------------|
        | id       | UUID    | Primary key             |
        | name     | String  | User's full name        |
        | email    | String  | User's email address    |
        | password | String  | Hashed password         |
        | phone    | String  | Contact number          |
        | address  | String  | Postal address          |
        | role     | Enum    | USER, ADMIN             |

        ---

        ### Policy

        | Field             | Type     | Description                          |
        |------------------|----------|--------------------------------------|
        | id               | UUID     | Primary key                          |
        | name             | String   | Policy name                          |
        | description      | Text     | Detailed policy description          |
        | premiumAmount    | Decimal  | Premium cost                         |
        | coverageAmount   | Decimal  | Amount covered by policy             |
        | durationMonths   | Integer  | Policy duration in months            |
        | renewalPremiumRate | Decimal| Rate used for renewal calculations   |
        | createdAt        | DateTime | When the policy was added            |
        | category         | Enum     | LIFE, HEALTH, VEHICLE                |

        ---

        ### UserPolicy

        | Field        | Type     | Description                           |
        |-------------|----------|---------------------------------------|
        | id          | UUID     | Primary key                           |
        | user        | FK(User) | Policy owner                          |
        | policy      | FK(Policy)| Purchased policy                      |
        | startDate   | Date     | Start of policy coverage              |
        | endDate     | Date     | End of policy coverage                |
        | status      | Enum     | ACTIVE, EXPIRED, CANCELLED            |
        | premiumPaid | Decimal  | Amount paid by user                   |

        ---

        ### Claim

        | Field          | Type         | Description                          |
        |----------------|--------------|--------------------------------------|
        | id             | UUID         | Primary key                          |
        | userPolicy     | FK(UserPolicy)| Policy on which claim is made       |
        | claimDate      | Date         | When the claim was submitted         |
        | claimAmount    | Decimal      | Requested claim amount               |
        | reason         | Text         | Reason for the claim                 |
        | status         | Enum         | PENDING, APPROVED, REJECTED, PROCESSING |
        | reviewerComment| Text         | Internal review notes                |
        | resolvedDate   | Date         | Date claim was resolved              |

        ---

        ### SupportTicket

        | Field       | Type         | Description                             |
        |-------------|--------------|-----------------------------------------|
        | id          | UUID         | Primary key                             |
        | user        | FK(User)     | User who created the ticket             |
        | policy      | FK(Policy)?  | Related policy (optional)               |
        | claim       | FK(Claim)?   | Related claim (optional)                |
        | subject     | String       | Brief summary of the issue              |
        | description | Text         | Full description of the issue           |
        | status      | Enum         | OPEN, RESOLVED, CLOSED, PENDING_USER_RESPONSE |
        | response    | Text         | Support response                        |
        | createdAt   | DateTime     | When ticket was created                 |
        | resolvedAt  | DateTime     | When ticket was resolved                |

    *   **Services:**
        *   **UserService (`user` package):** Handles user registration, login, and retrieval.
            *   `UserServiceImpl`
        *   **PolicyService (`policy` package):** Manages CRUD operations for insurance policies.
            *   `PolicyServiceImpl`
        *   **UserPolicyPurchaseService (`PolicyPurchaseService` package):** Handles the logic for users purchasing policies and managing their purchased policies.
            *   `UserPolicyPurchaseImpl`
        *   **PolicyRenewalService (`PolicyRenewal` package):** Manages the renewal of policies.
            *   `PolicyRenewalService` (implements `IPolicyRenewalService`)
        *   **ClaimManagementService (`ClaimManagement` package):** Handles submission, retrieval, and status updates for claims.
            *   `ClaimManagementServiceImplementation` (implements `ClaimManagementService`)
        *   **SupportTicketService (`supportTicket` package):** Manages CRUD operations and status updates for support tickets.
            *   `SupportTicketService` (implements `ISupportTicketService`)
        *   **JwtService (`security.jwt` package):** Handles JWT token generation and validation.
    *   **Repositories:**
        *   `UserRepository`: Interacts with the `app_user` table.
        *   `PolicyRepository`: Interacts with the `policy` table.
        *   `UserPolicyRepository`: Interacts with the `user_policy` table.
        *   `ClaimManagementRepository`: Interacts with the `claim` table.
        *   `SupportTicketRepository`: Interacts with the `support_ticket` table.
*   **Frontend (Vue.js):**
    *   **Components:**
        *   `auth/`: Components for login and registration.
        *   `claims/ClaimCard.vue`: Displays individual claim information.
        *   `core/HelloWorld.vue`, `core/NavBar.vue`: Basic UI elements.
        *   `policy/PolicyCard.vue`, `policy/PolicyDetailsModal.vue`, `policy/PurchaseConfirmation.vue`: Components for displaying, detailing, and confirming policy purchases.
        *   `support/AdminClaimsCard.vue`, `support/TicketCard.vue`, `support/TicketDetails.vue`, `support/TicketListItem.vue`: Components for managing support tickets and claims from an admin perspective.
        *   `ui/Button.vue`, `ui/Card.vue`, `ui/ReusableSelect.vue`, `ui/StatsSummary.vue`, `ui/StatusBadge.vue`, `ui/StatusFilter.vue`: Reusable UI elements.
        *   `App.vue`: Root Vue component.
    *   **Routing:**
        *   Managed by `vue-router` (defined in `frontend/src/router.ts`).
        *   `HomePage.vue`: Main landing page.
        *   `NotFoundPage.vue`: For 404 errors.
        *   `UnauthorizedPage.vue`: For access denied errors.
        *   `auth/LoginPage.vue`, `auth/RegisterPage.vue`: User authentication pages.
        *   `claims/AdminClaims.vue`, `claims/ClaimList.vue`, `claims/SubmitClaim.vue`: Pages for managing and submitting claims.
        *   `policy/PolicyCatalog.vue`, `policy/PolicyRenewList.vue`, `policy/RenewPolicyPage.vue`, `policy/UserPolicies.vue`, `policy/UserPurchasedPolicy.vue`: Pages for viewing, purchasing, and managing policies.
        *   `support/AdminTicketList.vue`, `support/SupportForm.vue`, `support/TicketList.vue`: Pages for managing and creating support tickets.
    *   **State Management:**
        *   (No explicit Vuex setup found in `package.json` or file structure. State might be managed via composables or prop drilling.)
    *   **API Integration:**
        *   Uses `axios` for making HTTP requests to the backend (configured in `frontend/src/utils/apis.ts`).
        *   API service functions are defined in `frontend/src/services/api.ts` and `frontend/src/services/auth.ts`.
*   **Database:**
    *   **Schema Design:**
        *   **app_user:** Stores user information (id, name, email, password, phone, address, role).
        *   **policy:** Stores details of insurance policies (id, name, description, premiumAmount, coverageAmount, durationMonths, renewalPremiumRate, createdAt, category).
        *   **user_policy:** Links users to policies they've purchased (id, user_id, policy_id, startDate, endDate, status, premiumPaid).
        *   **claim:** Stores claims made by users (id, user_policy_id, claimDate, claimAmount, reason, status, reviewerComment, resolvedDate).
        *   **support_ticket:** Stores support tickets (id, user_id, policy_id, claim_id, subject, description, status, response, createdAt, resolvedAt).
        *   Relationships are managed via foreign keys (e.g., `user_policy.user_id` references `app_user.id`).
        *   Hibernate (`spring.jpa.hibernate.ddl-auto=update`) manages schema generation based on entity definitions.

## 3. Test Plan

*   **Unit Tests:**
    *   Backend: Testing individual components and functions in isolation (using JUnit and Mockito, with H2 as an in-memory database for tests).
    *   Frontend: (No specific testing framework like Jest or Vitest explicitly configured in `package.json`, but `vue-tsc` is used for type checking).

*   **Test Cases:**

    ## 1. UserServiceTest.java (Service Layer)

    ### `register_shouldCreateUserSuccessfully`
    - **Scenario:** Registering a new user with valid details
    - **Input:** New `User` object with name, email, and password
    - **Expected:** User is saved, role is set to `USER`, password is encoded

    ### `register_shouldThrowException_whenUserAlreadyExists`
    - **Scenario:** Attempting to register a user with an email that already exists
    - **Input:** `User` object with an existing email
    - **Expected:** `UserAlreadyExistException` is thrown

    ### `login_shouldReturnToken_whenCredentialsAreValid`
    - **Scenario:** Logging in with correct email and password
    - **Input:** `User` object with valid email and password
    - **Expected:** JWT token is generated and returned

    ### `login_shouldThrowException_whenUserNotFound`
    - **Scenario:** Attempting to log in with a non-existent email
    - **Input:** `User` object with an email not in the database
    - **Expected:** `InvalidCredentialsException` is thrown

    ### `login_shouldThrowException_whenAuthenticationFails`
    - **Scenario:** Attempting to log in with an existing email but incorrect password
    - **Input:** `User` object with a valid email and wrong password
    - **Expected:** `InvalidCredentialsException` is thrown (due to `BadCredentialsException` from `AuthenticationManager`)

    ### `getAllUsers_shouldReturnAllUsers`
    - **Scenario:** Fetching all registered users
    - **Input:** None
    - **Expected:** A list of all `User` objects

    ### `getUserById_shouldReturnUser_whenUserExists`
    - **Scenario:** Fetching a user by their ID
    - **Input:** Existing user ID
    - **Expected:** The corresponding `User` object

    ### `getUserById_shouldReturnNull_whenUserNotExists`
    - **Scenario:** Fetching a user by a non-existent ID
    - **Input:** Non-existent user ID
    - **Expected:** `null` is returned

    ### `getCurrentEmail_shouldReturnEmail_whenUserDetailsAvailable`
    - **Scenario:** Get current user's email from `SecurityContextHolder` when `UserDetails` is available
    - **Input:** Mocked `SecurityContext` with `UserDetails`
    - **Expected:** User's email string

    ### `getCurrentUserId_shouldReturnUserId_whenTokenAvailable`
    - **Scenario:** Get current user's ID from JWT token in `SecurityContextHolder`
    - **Input:** Mocked `SecurityContext` with JWT in credentials
    - **Expected:** User's ID (Long)

    ### `getUserByEmail_shouldReturnUser_whenUserExists`
    - **Scenario:** Fetching a user by their email
    - **Input:** Existing user email
    - **Expected:** The corresponding `User` object

    ### `convertToDTO_shouldConvertUserToDTO`
    - **Scenario:** Converting a `User` entity to `UserDTO`
    - **Input:** `User` object
    - **Expected:** `UserDTO` with corresponding fields

    ---

    ## 2. UserPolicyPurchaseServiceTest.java (Service Layer)

    ### `purchaseAPolicy_shouldSucceed_whenValidUserAndPolicyAndNotAlreadyPurchased`
    - **Scenario:** A user purchases an available policy
    - **Input:** Valid `policyId`, `userId`. Policy not previously purchased by the user
    - **Expected:** `UserPolicy` object is created with `ACTIVE` status, correct start/end dates, and premium paid

    ### `purchaseAPolicy_shouldFail_whenAlreadyPurchased`
    - **Scenario:** A user attempts to purchase a policy they already own
    - **Input:** `policyId`, `userId` for an already purchased policy
    - **Expected:** `ResourceNotFoundException` (or similar, indicating policy already purchased/not available for purchase)

    ### `purchaseAPolicy_shouldFail_whenPolicyNotFound`
    - **Scenario:** A user attempts to purchase a non-existent policy
    - **Input:** Non-existent `policyId`, valid `userId`
    - **Expected:** `ResourceNotFoundException`

    ### `purchaseAPolicy_shouldFail_whenUserNotFound`
    - **Scenario:** A non-existent user attempts to purchase a policy
    - **Input:** Valid `policyId`, non-existent `userId`
    - **Expected:** `ResourceNotFoundException`

    ### `getPurchasedPolicies_shouldReturnPolicies_whenUserExists`
    - **Scenario:** Fetching all policies purchased by a specific user
    - **Input:** Existing `userId`
    - **Expected:** A list of `UserPolicy` objects belonging to the user

    ### `getPurchasedPolicies_shouldFail_whenUserNotFound`
    - **Scenario:** Fetching policies for a non-existent user
    - **Input:** Non-existent `userId`
    - **Expected:** `ResourceNotFoundException`

    ### `updatePolicy_shouldCancelPolicy`
    - **Scenario:** Cancelling an active user policy
    - **Input:** `userId`, `policyId` of an active policy, new status `CANCELLED`
    - **Expected:** `UserPolicy` status is updated to `CANCELLED`, and `endDate` is set to the current date

    ### `updatePolicy_shouldRenewPolicy`
    - **Scenario:** Renewing a user policy
    - **Input:** `userId`, `policyId`, new status `RENEWED`
    - **Expected:** `UserPolicy` status is updated to `RENEWED` (or `ACTIVE`), and `endDate` is extended based on policy duration

    ### `updatePolicy_shouldFail_whenUserPolicyNotFound`
    - **Scenario:** Attempting to update a non-existent user policy
    - **Input:** Non-existent `userId` or `policyId`
    - **Expected:** `ResourceNotFoundException`

    ---

    ## 3. SupportTicketServiceTest.java (Service Layer)

    ### `createTicket_shouldSetDefaultsAndSave`
    - **Scenario:** Creating a new support ticket with minimal information
    - **Input:** `SupportTicket` object with user, subject, and description
    - **Expected:** Ticket is saved with `OPEN` status and `createdAt` timestamp

    ### `createTicket_shouldPreserveUserAndPolicyData`
    - **Scenario:** Creating a ticket with associated user and policy
    - **Input:** `SupportTicket` with user, policy, subject, description
    - **Expected:** All provided data is preserved, status is `OPEN`

    ### `updateTicket_shouldUpdateAndSave`
    - **Scenario:** Updating an open support ticket to `RESOLVED`
    - **Input:** `ticketId`, response message, new status `RESOLVED`
    - **Expected:** Ticket response and status are updated, `resolvedAt` timestamp is set

    ### `updateTicket_shouldSetResolvedAtWhenStatusIsClosed`
    - **Scenario:** Updating an open support ticket to `CLOSED`
    - **Input:** `ticketId`, response message, new status `CLOSED`
    - **Expected:** Ticket response and status are updated, `resolvedAt` timestamp is set

    ### `updateTicket_shouldThrowNotFound`
    - **Scenario:** Attempting to update a non-existent ticket
    - **Input:** Non-existent `ticketId`
    - **Expected:** `TicketNotFoundException`

    ### `updateTicket_shouldThrowAlreadyClosedException`
    - **Scenario:** Attempting to update a ticket that is already `CLOSED`
    - **Input:** `ticketId` of a closed ticket
    - **Expected:** `TicketAlreadyClosedException`

    ### `getTicketById_shouldReturnTicket`
    - **Scenario:** Fetching an existing ticket by its ID
    - **Input:** Existing `ticketId`
    - **Expected:** The corresponding `SupportTicket` object

    ### `getAllTickets_shouldReturnAllTickets`
    - **Scenario:** Fetching all support tickets
    - **Input:** None
    - **Expected:** A list of all `SupportTicket` objects

    ### `deleteTicket_shouldDeleteSuccessfully`
    - **Scenario:** Deleting an existing ticket
    - **Input:** Existing `ticketId`
    - **Expected:** Ticket is deleted from the repository

    ### `updateTicket_shouldNotAllowClosedTicketReopening`
    - **Scenario:** Attempting to change status of a `CLOSED` ticket to `OPEN`
    - **Input:** `ticketId` of a closed ticket, new status `OPEN`
    - **Expected:** `TicketAlreadyClosedException`

    ### `updateTicket_shouldRejectRedundantResolvedUpdate`
    - **Scenario:** Attempting to update a `RESOLVED` ticket to `RESOLVED` again
    - **Input:** `ticketId` of a resolved ticket, new status `RESOLVED`
    - **Expected:** `InvalidTicketTransitionException`

    ---

    ## 4. PolicyServiceTest.java (Service Layer)

    ### `createPolicy_shouldSavePolicySuccessfully`
    - **Scenario:** Creating a new insurance policy with all details
    - **Input:** `Policy` object with name, description, amounts, duration, rate, category
    - **Expected:** Policy is saved with a generated ID and `createdAt` timestamp

    ### `createPolicy_shouldHandleAllCategories`
    - **Scenario:** Creating policies for different categories (LIFE, HEALTH, VEHICLE)
    - **Input:** `Policy` objects with different categories
    - **Expected:** Policies are saved with their respective categories

    ### `createPolicy_shouldHandleNullValues`
    - **Scenario:** Creating a policy with some fields being null
    - **Input:** `Policy` object with name, other fields null
    - **Expected:** Policy is saved, null fields remain null

    ### `getAllPolicies_shouldReturnAllPolicies`
    - **Scenario:** Fetching all available insurance policies
    - **Input:** None
    - **Expected:** A list of all `Policy` objects

    ### `getAllPolicies_shouldReturnEmptyList_whenNoPolicies`
    - **Scenario:** Fetching policies when none exist
    - **Input:** None
    - **Expected:** An empty list

    > **Note:** Other tests for `getPolicyById`, `updatePolicy`, `deletePolicy` would follow a similar pattern: test success, not found, invalid input scenarios.

    ---

    ## 5. ClaimManagementControllerTest.java (Controller Layer - MockMVC)

    ### `submitClaim_shouldReturnCreatedClaim`
    - **Scenario:** User submits a new claim
    - **Input:** HTTP POST to `/claim` with `UserClaimDTO` (userPolicyId, claimAmount, reason)
    - **Expected:** HTTP 200 OK, response body contains the created `Claim` details

    ### `getAllClaims_shouldReturnListOfClaims`
    - **Scenario:** Admin (or user, depending on endpoint actual role) requests all claims
    - **Input:** HTTP GET to `/user/claim`
    - **Expected:** HTTP 200 OK, response body is a list of `Claim` objects

    ### `getClaimsByUser_shouldReturnUserClaims`
    - **Scenario:** User requests their own claims
    - **Input:** HTTP GET to `/user/claimById` (authenticated user)
    - **Expected:** HTTP 200 OK, response body is a list of `Claim` objects for that user

    ### `updateClaimStatus_shouldReturnUpdatedClaim`
    - **Scenario:** Admin updates the status of a claim
    - **Input:** HTTP PUT to `/claim/{claimId}/status` with `AdminClaimStatusUpdateDTO` (status, reviewerComment)
    - **Expected:** HTTP 200 OK, response body contains the updated `Claim` details

    ### `deleteClaim_shouldDeleteSuccessfully`
    - **Scenario:** Admin deletes a claim
    - **Input:** HTTP DELETE to `/claim/{claimId}`
    - **Expected:** HTTP 200 OK

    ---

    ## 6. PolicyControllerTest.java (Controller Layer - MockMVC)

    ### `getAllPolicies_shouldReturnListOfPolicies`
    - **Scenario:** Requesting all available insurance policies
    - **Input:** HTTP GET to `/policies`
    - **Expected:** HTTP 200 OK, response body is a list of `Policy` objects

    ### `getPolicyById_shouldReturnPolicy_whenPolicyExists`
    - **Scenario:** Requesting a specific policy by its ID
    - **Input:** HTTP GET to `/policies/{id}` with an existing policy ID
    - **Expected:** HTTP 200 OK, response body contains the `Policy` details

    ### `createPolicy_shouldReturnCreatedPolicy`
    - **Scenario:** Admin creates a new policy
    - **Input:** HTTP POST to `/policies/create` with `Policy` data
    - **Expected:** HTTP 201 Created, response body contains the created `Policy` details

    ### `updatePolicy_shouldReturnUpdatedPolicy`
    - **Scenario:** Admin updates an existing policy
    - **Input:** HTTP PUT to `/policies/update/{id}` with updated `Policy` data
    - **Expected:** HTTP 200 OK, response body contains the updated `Policy` details

    ### `deletePolicy_shouldReturnSuccessMessage`
    - **Scenario:** Admin deletes a policy
    - **Input:** HTTP DELETE to `/policies/delete/{id}`
    - **Expected:** HTTP 200 OK, response body is "Policy deleted successfully"

    ---

    ## 7. SupportTicketControllerTest.java (Controller Layer - MockMVC)

    ### `createTicket_shouldReturnCreatedTicket_withNoPolicyOrClaim`
    - **Scenario:** User creates a support ticket without linking to a policy or claim
    - **Input:** HTTP POST to `/support` with `CreateSupportTicketRequest` (subject, description)
    - **Expected:** HTTP 200 OK, response body contains the created `SupportTicket` details

    ### `createTicket_shouldReturnCreatedTicket_withPolicy`
    - **Scenario:** User creates a support ticket linked to a policy
    - **Input:** HTTP POST to `/support` with `CreateSupportTicketRequest` (subject, description, policyId)
    - **Expected:** HTTP 200 OK, response body contains the created `SupportTicket` with policy details

    ### `getTicketsByUser_shouldReturnListOfTickets`
    - **Scenario:** User requests their own support tickets
    - **Input:** HTTP GET to `/support/user` (authenticated user)
    - **Expected:** HTTP 200 OK, response body is a list of `SupportTicket` objects for that user

    ### `updateTicket_shouldReturnUpdatedTicket`
    - **Scenario:** Admin updates a support ticket (e.g., adds response, changes status)
    - **Input:** HTTP PUT to `/support/{ticketId}` with `UpdateSupportTicketRequest` (response, status)
    - **Expected:** HTTP 200 OK, response body contains the updated `SupportTicket` details

    ### `getAllTickets_shouldReturnListOfAllTickets`
    - **Scenario:** Admin requests all support tickets
    - **Input:** HTTP GET to `/support`
    - **Expected:** HTTP 200 OK, response body is a list of all `SupportTicket` objects

    ### `deleteTicket_shouldReturnNoContent`
    - **Scenario:** Admin deletes a support ticket
    - **Input:** HTTP DELETE to `/support/{ticketId}`
    - **Expected:** HTTP 204 No Content

    ---

    ## 8. UserControllerTest.java (Controller Layer - MockMVC)

    ### `register_shouldReturnCreatedUser`
    - **Scenario:** A new user registers
    - **Input:** HTTP POST to `/auth/register` with user details (name, email, password, phone, address)
    - **Expected:** HTTP 201 Created, response body contains `UserDTO` of the created user

    ### `register_shouldHandleUserAlreadyExistsException`
    - **Scenario:** Attempting to register with an email that already exists
    - **Input:** HTTP POST to `/auth/register` with an existing email
    - **Expected:** HTTP 409 Conflict (or appropriate error status based on global exception handler) with message "User already exists"

    ### `login_shouldReturnTokenAndUserDetails`
    - **Scenario:** User logs in with valid credentials
    - **Input:** HTTP POST to `/auth/login` with email and password
    - **Expected:** HTTP 200 OK, response body contains JWT token and `UserDTO`

    ### `login_shouldHandleInvalidCredentialsException`
    - **Scenario:** User logs in with invalid credentials
    - **Input:** HTTP POST to `/auth/login` with incorrect email or password
    - **Expected:** HTTP 401 Unauthorized (or appropriate error status) with message "Invalid credentials"

    ---

    ## 9. UserPolicyPurchaseControllerTest.java (Controller Layer - MockMVC)

    ### `purchasePolicy_shouldReturnCreatedPolicy`
    - **Scenario:** User purchases an insurance policy
    - **Input:** HTTP POST to `/user/policy/{policyId}/purchase` (authenticated user)
    - **Expected:** HTTP 201 Created, response body contains the created `UserPolicy` details

    ### `getUserPolicies_shouldReturnListOfPolicies`
    - **Scenario:** User requests their list of purchased policies
    - **Input:** HTTP GET to `/user/policy` (authenticated user)
    - **Expected:** HTTP 200 OK, response body is a list of `UserPolicy` objects

    ### `updatePolicy_shouldReturnUpdatedPolicy`
    - **Scenario:** User updates the status of one of their policies (e.g., cancels it)
    - **Input:** HTTP PUT to `/user/policy` with query parameters `policyId` and `status`
    - **Expected:** HTTP 200 OK, response body contains the updated `UserPolicy` details

    ---

## 10. DigitalInsuranceManagementSystemApplicationTests.java

### `contextLoads`
- **Scenario:** Verifies that the Spring application context loads successfully
- **Input:** None
- **Expected:** Test passes if the context loads without errors

## 4. Setup and Configuration

*   **Backend Setup:**
    *   **Prerequisites:**
        *   Java 17
        *   Maven
    *   **Steps to run the backend:**
        ```bash
        cd backend
        ./mvnw spring-boot:run
        ```
*   **Frontend Setup:**
    *   **Prerequisites:**
        *   Node.js
        *   npm
    *   **Steps to run the frontend:**
        ```bash
        cd frontend
        npm install
        npm run dev 
        ```
        (Note: `npm run serve` is often used with Vue CLI, but Vite uses `npm run dev` by default as seen in [package.json](http://_vscodecontentref_/0))
*   **Database Setup:**
    *   The application uses MySQL, which is configured to run in a Docker container via [docker-compose.yml](http://_vscodecontentref_/1).
    *   **Steps to run the database (and other services via Docker Compose):**
        ```bash
        docker-compose up -d mysql-docker # To start only MySQL
        # or to start all services defined in docker-compose.yml
        docker-compose up -d
        ```
    *   **Database Connection Details (from [application.properties](http://_vscodecontentref_/2) and [docker-compose.yml](http://_vscodecontentref_/3)):**
        *   Driver: `com.mysql.cj.jdbc.Driver`
        *   URL: `jdbc:mysql://mysql-docker:3306/DIMS` (when running with Docker Compose) or `${DATABASE_URL}` (can be set as environment variable)
        *   Username: `root` (for Docker Compose) or `${DATABASE_USER}`
        *   Password: `12345` (for Docker Compose) or `${DATABASE_PASSWORD}`
        *   The backend service in [docker-compose.yml](http://_vscodecontentref_/4) is configured to connect to `mysql-docker` on port `3306` with database `DIMS`, user `root`, and password `12345`.

## 5. API Documentation

### Authentication Endpoints (`/auth`)

#### 1. Register User

*   **Endpoint:** `/auth/register`
*   **Method:** `POST`
*   **Request Body:**
    ```json
    {
      "name": "John Doe",
      "email": "john.doe@example.com",
      "password": "password123",
      "phone": "1234567890",
      "address": "123 Main St"
    }
    ```
*   **Response Body (Success - 201 Created):**
    ```json
    {
      "id": 1,
      "name": "John Doe",
      "email": "john.doe@example.com",
      "role": "USER"
    }
    ```
*   **Response Body (Error - 409 Conflict):**
    ```text
    User already exists
    ```
*   **Example Request (cURL):**
    ```bash
    curl -X POST -H "Content-Type: application/json" -d '{
      "name": "John Doe",
      "email": "john.doe@example.com",
      "password": "password123",
      "phone": "1234567890",
      "address": "123 Main St"
    }' http://localhost:8081/auth/register
    ```
*   **Example Response (Success):**
    ```json
    {
      "id": 1,
      "name": "John Doe",
      "email": "john.doe@example.com",
      "role": "USER"
    }
    ```

#### 2. Login User

*   **Endpoint:** `/auth/login`
*   **Method:** `POST`
*   **Request Body:**
    ```json
    {
      "email": "john.doe@example.com",
      "password": "password123"
    }
    ```
*   **Response Body (Success - 200 OK):**
    ```json
    {
      "token": "jwt.token.string",
      "user": {
        "id": 1,
        "name": "John Doe",
        "email": "john.doe@example.com",
        "role": "USER"
      }
    }
    ```
*   **Response Body (Error - 401 Unauthorized):**
    ```text
    Invalid credentials
    ```
*   **Example Request (cURL):**
    ```bash
    curl -X POST -H "Content-Type: application/json" -d '{
      "email": "john.doe@example.com",
      "password": "password123"
    }' http://localhost:8081/auth/login
    ```
*   **Example Response (Success):**
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInJvbGUiOiJVU0VSIiwic3ViIjoiam9obi5kb2VAZXhhbXBsZS5jb20iLCJpYXQiOjE2NzgzNzQwNzYsImV4cCI6MTY3ODM3NzY3Nn0.exampleToken",
      "user": {
        "id": 1,
        "name": "John Doe",
        "email": "john.doe@example.com",
        "role": "USER"
      }
    }
    ```

### Policy Endpoints (`/policies`)

#### 1. Get All Policies

*   **Endpoint:** `/policies`
*   **Method:** `GET`
*   **Request Body:** None
*   **Response Body (Success - 200 OK):**
    ```json
    [
      {
        "id": 1,
        "name": "Life Insurance Premium",
        "description": "Comprehensive life insurance coverage",
        "premiumAmount": 500.00,
        "coverageAmount": 100000.00,
        "durationMonths": 12,
        "renewalPremiumRate": 0.05,
        "createdAt": "2025-05-26T10:00:00",
        "category": "LIFE"
      },
      {
        "id": 2,
        "name": "Health Insurance Basic",
        "description": "Basic health insurance coverage",
        "premiumAmount": 300.00,
        "coverageAmount": 50000.00,
        "durationMonths": 12,
        "renewalPremiumRate": 0.03,
        "createdAt": "2025-05-26T10:05:00",
        "category": "HEALTH"
      }
    ]
    ```
*   **Example Request (cURL):**
    ```bash
    curl -X GET -H "Authorization: Bearer jwt.token.string" http://localhost:8081/policies
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 2. Get Policy By ID

*   **Endpoint:** `/policies/{id}`
*   **Method:** `GET`
*   **Request Body:** None
*   **Response Body (Success - 200 OK):**
    ```json
    {
      "id": 1,
      "name": "Life Insurance Premium",
      "description": "Comprehensive life insurance coverage",
      "premiumAmount": 500.00,
      "coverageAmount": 100000.00,
      "durationMonths": 12,
      "renewalPremiumRate": 0.05,
      "createdAt": "2025-05-26T10:00:00",
      "category": "LIFE"
    }
    ```
*   **Response Body (Error - 404 Not Found if policy doesn't exist, or 200 OK with null/empty body if service returns null):** (Behavior might vary based on service implementation for not found)
*   **Example Request (cURL):**
    ```bash
    curl -X GET -H "Authorization: Bearer jwt.token.string" http://localhost:8081/policies/1
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 3. Create Policy (Admin Only)

*   **Endpoint:** `/policies/create`
*   **Method:** `POST`
*   **Authorization:** Requires ADMIN role.
*   **Request Body:**
    ```json
    {
      "name": "Vehicle Insurance Gold",
      "description": "Full coverage for vehicles",
      "premiumAmount": 750.00,
      "coverageAmount": 25000.00,
      "durationMonths": 12,
      "renewalPremiumRate": 0.06,
      "category": "VEHICLE"
    }
    ```
*   **Response Body (Success - 201 Created):**
    ```json
    {
      "id": 3,
      "name": "Vehicle Insurance Gold",
      "description": "Full coverage for vehicles",
      "premiumAmount": 750.00,
      "coverageAmount": 25000.00,
      "durationMonths": 12,
      "renewalPremiumRate": 0.06,
      "createdAt": "2025-05-26T10:10:00",
      "category": "VEHICLE"
    }
    ```
*   **Example Request (cURL):**
    ```bash
    curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer admin.jwt.token.string" -d '{
      "name": "Vehicle Insurance Gold",
      "description": "Full coverage for vehicles",
      "premiumAmount": 750.00,
      "coverageAmount": 25000.00,
      "durationMonths": 12,
      "renewalPremiumRate": 0.06,
      "category": "VEHICLE"
    }' http://localhost:8081/policies/create
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 4. Update Policy (Admin Only)

*   **Endpoint:** `/policies/update/{id}`
*   **Method:** `PUT`
*   **Authorization:** Requires ADMIN role.
*   **Request Body:**
    ```json
    {
      "name": "Life Insurance Premium Plus",
      "description": "Enhanced life insurance coverage",
      "premiumAmount": 550.00,
      "coverageAmount": 120000.00,
      "durationMonths": 12,
      "renewalPremiumRate": 0.055,
      "category": "LIFE"
    }
    ```
*   **Response Body (Success - 200 OK):**
    ```json
    {
      "id": 1,
      "name": "Life Insurance Premium Plus",
      "description": "Enhanced life insurance coverage",
      "premiumAmount": 550.00,
      "coverageAmount": 120000.00,
      "durationMonths": 12,
      "renewalPremiumRate": 0.055,
      "createdAt": "2025-05-26T10:00:00", 
      "category": "LIFE"
    }
    ```
*   **Response Body** (Error - 404 Not Found if policy doesn't exist)
*   **Example Request (cURL):**
    ```bash
    curl -X PUT -H "Content-Type: application/json" -H "Authorization: Bearer admin.jwt.token.string" -d '{
      "name": "Life Insurance Premium Plus",
      "description": "Enhanced life insurance coverage",
      "premiumAmount": 550.00,
      "coverageAmount": 120000.00,
      "durationMonths": 12,
      "renewalPremiumRate": 0.055,
      "category": "LIFE"
    }' http://localhost:8081/policies/update/1
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 5. Delete Policy (Admin Only)

*   **Endpoint:** `/policies/delete/{id}`
*   **Method:** `DELETE`
*   **Authorization:** Requires ADMIN role.
*   **Request Body:** None
*   **Response Body (Success - 200 OK):**
    ```text
    Policy deleted successfully
    ```
*   **Response Body** (Error - 404 Not Found if policy doesn't exist, or if service throws an exception for deletion failure)
*   **Example Request (cURL):**
    ```bash
    curl -X DELETE -H "Authorization: Bearer admin.jwt.token.string" http://localhost:8081/policies/delete/1
    ```
*   **Example Response (Success):**
    ```text
    Policy deleted successfully
    ```

### User Policy Purchase Endpoints (`/user/policy`)

#### 1. Purchase a Policy

*   **Endpoint:** `/user/policy/{policyId}/purchase`
*   **Method:** `POST`
*   **Request Header:** `Authorization: Bearer <user.jwt.token.string>`
*   **Request Body:** None
*   **Response Body (Success - 201 Created):**
    ```json
    {
        "id": 1,
        "user": {
            "id": 101,
            "name": "Current User",
            "email": "current.user@example.com",
            "role": "USER"
            // other user fields excluded for brevity
        },
        "policy": {
            "id": 202,
            "name": "Health Insurance Basic",
            "description": "Basic health insurance coverage",
            "premiumAmount": 300.00,
            "coverageAmount": 50000.00,
            "durationMonths": 12,
            "renewalPremiumRate": 0.03,
            "createdAt": "2025-05-26T10:05:00",
            "category": "HEALTH"
        },
        "startDate": "2025-05-26",
        "endDate": "2026-05-26",
        "status": "ACTIVE",
        "premiumPaid": 300.00
    }
    ```
*   **Response Body (Error - 404 Not Found if policy or user doesn't exist, 400 Bad Request for other issues like already purchased)
*   **Example Request (cURL):**
    ```bash
    curl -X POST -H "Authorization: Bearer user.jwt.token.string" http://localhost:8081/user/policy/202/purchase
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 2. Get User's Purchased Policies

*   **Endpoint:** `/user/policy`
*   **Method:** `GET`
*   **Request Header:** `Authorization: Bearer <user.jwt.token.string>`
*   **Request Body:** None
*   **Response Body (Success - 200 OK):**
    ```json
    [
        {
            "id": 1,
            "user": { "id": 101, "name": "Current User", "email": "current.user@example.com", "role": "USER" },
            "policy": { "id": 202, "name": "Health Insurance Basic", "category": "HEALTH", "premiumAmount": 300.00 },
            "startDate": "2025-05-26",
            "endDate": "2026-05-26",
            "status": "ACTIVE",
            "premiumPaid": 300.00
        }
        // ... more user policies
    ]
    ```
*   **Example Request (cURL):**
    ```bash
    curl -X GET -H "Authorization: Bearer user.jwt.token.string" http://localhost:8081/user/policy
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 3. Update User Policy Status (e.g., Cancel)

*   **Endpoint:** `/user/policy`
*   **Method:** `PUT`
*   **Request Header:** `Authorization: Bearer <user.jwt.token.string>`
*   **Request Parameters:** `policyId={userPolicyId}&status={newStatus}` (e.g., `policyId=1&status=CANCELLED`)
*   **Request Body:** None
*   **Response Body (Success - 200 OK):**
    ```json
    {
        "id": 1,
        "user": { "id": 101, "name": "Current User", "email": "current.user@example.com", "role": "USER" },
        "policy": { "id": 202, "name": "Health Insurance Basic", "category": "HEALTH", "premiumAmount": 300.00 },
        "startDate": "2025-05-26",
        "endDate": "2026-05-26",
        "status": "CANCELLED", // Updated status
        "premiumPaid": 300.00
    }
    ```
*   **Response Body** (Error - 404 Not Found if user policy doesn't exist, 400 Bad Request for invalid status or other issues)
*   **Example Request (cURL):**
    ```bash
    curl -X PUT -H "Authorization: Bearer user.jwt.token.string" "http://localhost:8081/user/policy?policyId=1&status=CANCELLED"
    ```
*   **Example Response (Success):** (As shown in Response Body)

### Policy Renewal Endpoints

#### 1. Renew a Policy

*   **Endpoint:** `/policy/{policyId}/renew` (Note: `policyId` here refers to the `UserPolicy` ID)
*   **Method:** `POST`
*   **Request Header:** `Authorization: Bearer <user.jwt.token.string>` (Implicitly uses current user from token)
*   **Request Body:** None
*   **Response Body (Success - 200 OK):**
    ```json
    {
        "id": 1, // UserPolicy ID
        "user": { "id": 101, "name": "Current User", "email": "current.user@example.com", "role": "USER" },
        "policy": { "id": 202, "name": "Health Insurance Basic", "durationMonths": 12, "renewalPremiumRate": 330.00 }, // renewalPremiumRate is the new premium
        "startDate": "2025-05-26", // New start date
        "endDate": "2026-05-26",   // New end date
        "status": "ACTIVE", // Or RENEWED, depending on implementation
        "premiumPaid": 330.00 // New premium paid
    }
    ```
*   **Response Body (Error - 404 Not Found if UserPolicy doesn't exist, 400 Bad Request if not eligible for renewal)
*   **Example Request (cURL):**
    ```bash
    curl -X POST -H "Authorization: Bearer user.jwt.token.string" http://localhost:8081/policy/1/renew
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 2. Get Renewable Policies for Current User

*   **Endpoint:** `/user/policies/renewable`
*   **Method:** `GET`
*   **Request Header:** `Authorization: Bearer <user.jwt.token.string>`
*   **Request Body:** None
*   **Response Body (Success - 200 OK):**
    ```json
    [
        {
            "userPolicyId": 1,
            "policyName": "Health Insurance Basic",
            "endDate": "2025-06-15", // Expiring soon or expired
            "premiumPaid": 300.00, // Original premium
            "renewalPremiumRate": 330.00 // New premium for renewal
        }
        // ... more renewable policies
    ]
    ```
*   **Example Request (cURL):**
    ```bash
    curl -X GET -H "Authorization: Bearer user.jwt.token.string" http://localhost:8081/user/policies/renewable
    ```
*   **Example Response (Success):** (As shown in Response Body)

### Claim Management Endpoints

#### 1. Submit a Claim

*   **Endpoint:** `/claim`
*   **Method:** `POST`
*   **Request Header:** `Authorization: Bearer <user.jwt.token.string>` (User identified by token)
*   **Request Body (`UserClaimDTO`):**
    ```json
    {
        "userPolicyId": 1, // ID of the UserPolicy being claimed against
        "claimAmount": 150.75,
        "reason": "Hospital visit for consultation"
    }
    ```
*   **Response Body (Success - 200 OK):**
    ```json
    {
        "id": 501,
        "userPolicy": {
            "id": 1,
            "user": { "id": 101, "name": "Current User" },
            "policy": { "id": 202, "name": "Health Insurance Basic" }
            // ... other UserPolicy fields
        },
        "claimDate": "2025-05-26",
        "claimAmount": 150.75,
        "reason": "Hospital visit for consultation",
        "status": "PENDING",
        "reviewerComment": null,
        "resolvedDate": null
    }
    ```
*   **Response Body (Error - 404 Not Found if UserPolicy doesn't exist or doesn't belong to user, 400 Bad Request for invalid data)
*   **Example Request (cURL):**
    ```bash
    curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer user.jwt.token.string" -d '{
        "userPolicyId": 1,
        "claimAmount": 150.75,
        "reason": "Hospital visit for consultation"
    }' http://localhost:8081/claim
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 2. Get All Claims (Admin View - Potentially)

*   **Endpoint:** `/user/claim` (Endpoint name might be misleading if it's for admins)
*   **Method:** `GET`
*   **Request Header:** `Authorization: Bearer <admin.jwt.token.string>` (If admin restricted)
*   **Request Body:** None
*   **Response Body (Success - 200 OK):**
    ```json
    [
        {
            "id": 501,
            "userPolicy": { "id": 1, "user": { "id": 101 }, "policy": { "id": 202 } },
            "claimDate": "2025-05-26",
            "claimAmount": 150.75,
            "reason": "Hospital visit for consultation",
            "status": "PENDING",
            "reviewerComment": null,
            "resolvedDate": null
        }
        // ... more claims
    ]
    ```
*   **Example Request (cURL):**
    ```bash
    curl -X GET -H "Authorization: Bearer admin.jwt.token.string" http://localhost:8081/user/claim
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 3. Get Claims by Current User

*   **Endpoint:** `/user/claimById`
*   **Method:** `GET`
*   **Request Header:** `Authorization: Bearer <user.jwt.token.string>`
*   **Request Body:** None
*   **Response Body (Success - 200 OK):**
    ```json
    [
        {
            "id": 501,
            "userPolicy": { "id": 1, "user": { "id": 101 }, "policy": { "id": 202 } },
            "claimDate": "2025-05-26",
            "claimAmount": 150.75,
            "reason": "Hospital visit for consultation",
            "status": "PENDING",
            "reviewerComment": null,
            "resolvedDate": null
        }
        // ... more claims for the current user
    ]
    ```
*   **Example Request (cURL):**
    ```bash
    curl -X GET -H "Authorization: Bearer user.jwt.token.string" http://localhost:8081/user/claimById
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 4. Update Claim Status (Admin Only)

*   **Endpoint:** `/claim/{claimId}/status`
*   **Method:** `PUT`
*   **Authorization:** Requires ADMIN role.
*   **Request Header:** `Authorization: Bearer <admin.jwt.token.string>`
*   **Request Body (`AdminClaimStatusUpdateDTO`):**
    ```json
    {
        "status": "APPROVED", // e.g., PENDING, APPROVED, REJECTED, PROCESSING
        "reviewerComment": "Claim approved as per policy terms."
    }
    ```
*   **Response Body (Success - 200 OK):**
    ```json
    {
        "id": 501,
        "userPolicy": { "id": 1, "user": { "id": 101 }, "policy": { "id": 202 } },
        "claimDate": "2025-05-26",
        "claimAmount": 150.75,
        "reason": "Hospital visit for consultation",
        "status": "APPROVED", // Updated status
        "reviewerComment": "Claim approved as per policy terms.",
        "resolvedDate": "2025-05-27" // Or current date when resolved
    }
    ```
*   **Response Body (Error - 404 Not Found if claim doesn't exist, 400 Bad Request for invalid status)
*   **Example Request (cURL):**
    ```bash
    curl -X PUT -H "Content-Type: application/json" -H "Authorization: Bearer admin.jwt.token.string" -d '{
        "status": "APPROVED",
        "reviewerComment": "Claim approved as per policy terms."
    }' http://localhost:8081/claim/501/status
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 5. Delete Claim (Admin Only)

*   **Endpoint:** `/claim/{id}`
*   **Method:** `DELETE`
*   **Authorization:** Requires ADMIN role.
*   **Request Header:** `Authorization: Bearer <admin.jwt.token.string>`
*   **Request Body:** None
*   **Response Body (Success - 200 OK):** Empty body or a success message (controller returns `ResponseEntity.ok().build()` which is empty).
*   **Response Body (Error - 404 Not Found if claim doesn't exist)
*   **Example Request (cURL):**
    ```bash
    curl -X DELETE -H "Authorization: Bearer admin.jwt.token.string" http://localhost:8081/claim/501
    ```
*   **Example Response (Success):** (Status 200 OK with no content)

### Support Ticket Endpoints (`/support`)

#### 1. Create Support Ticket

*   **Endpoint:** `/support`
*   **Method:** `POST`
*   **Request Header:** `Authorization: Bearer <user.jwt.token.string>`
*   **Request Body (`CreateSupportTicketRequest`):**
    ```json
    {
        "subject": "Issue with policy document",
        "description": "I cannot find the policy document for my Health Insurance Basic policy.",
        "policyId": 202, // Optional: ID of the master Policy
        "claimId": null   // Optional: ID of the Claim
    }
    ```
*   **Response Body (Success - 200 OK, `SupportTicketResponse`):**
    ```json
    {
        "id": 701,
        "userId": 101,
        "policyId": 202,
        "claimId": null,
        "subject": "Issue with policy document",
        "description": "I cannot find the policy document for my Health Insurance Basic policy.",
        "status": "OPEN",
        "response": null,
        "createdAt": "2025-05-26T11:00:00",
        "resolvedAt": null
    }
    ```
*   **Response Body (Error - 403 Forbidden if policy/claim does not belong to user, 404 Not Found if user/policy/claim not found)
*   **Example Request (cURL):**
    ```bash
    curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer user.jwt.token.string" -d '{
        "subject": "Issue with policy document",
        "description": "I cannot find the policy document for my Health Insurance Basic policy.",
        "policyId": 202
    }' http://localhost:8081/support
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 2. Get Support Tickets for Current User

*   **Endpoint:** `/support/user`
*   **Method:** `GET`
*   **Request Header:** `Authorization: Bearer <user.jwt.token.string>`
*   **Request Body:** None
*   **Response Body (Success - 200 OK, List of `SupportTicketResponse`):**
    ```json
    [
        {
            "id": 701,
            "userId": 101,
            "policyId": 202,
            "claimId": null,
            "subject": "Issue with policy document",
            "description": "I cannot find the policy document for my Health Insurance Basic policy.",
            "status": "OPEN",
            "response": null,
            "createdAt": "2025-05-26T11:00:00",
            "resolvedAt": null
        }
        // ... more tickets for the user
    ]
    ```
*   **Example Request (cURL):**
    ```bash
    curl -X GET -H "Authorization: Bearer user.jwt.token.string" http://localhost:8081/support/user
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 3. Update Support Ticket (Admin Only)

*   **Endpoint:** `/support/{ticketId}`
*   **Method:** `PUT`
*   **Authorization:** Requires ADMIN role.
*   **Request Header:** `Authorization: Bearer <admin.jwt.token.string>`
*   **Request Body (`UpdateSupportTicketRequest`):**
    ```json
    {
        "response": "We have resent the policy document to your registered email address.",
        "status": "RESOLVED" // e.g., OPEN, RESOLVED, CLOSED, PENDING_USER_RESPONSE
    }
    ```
*   **Response Body (Success - 200 OK, `SupportTicketResponse`):**
    ```json
    {
        "id": 701,
        "userId": 101,
        "policyId": 202,
        "claimId": null,
        "subject": "Issue with policy document",
        "description": "I cannot find the policy document for my Health Insurance Basic policy.",
        "status": "RESOLVED",
        "response": "We have resent the policy document to your registered email address.",
        "createdAt": "2025-05-26T11:00:00",
        "resolvedAt": "2025-05-26T11:05:00"
    }
    ```
*   **Response Body (Error - 404 Not Found if ticket doesn't exist, 400 Bad Request for invalid status or other issues)
*   **Example Request (cURL):**
    ```bash
    curl -X PUT -H "Content-Type: application/json" -H "Authorization: Bearer admin.jwt.token.string" -d '{
        "response": "We have resent the policy document to your registered email address.",
        "status": "RESOLVED"
    }' http://localhost:8081/support/701
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 4. Get All Support Tickets (Admin Only)

*   **Endpoint:** `/support`
*   **Method:** `GET`
*   **Authorization:** Requires ADMIN role.
*   **Request Header:** `Authorization: Bearer <admin.jwt.token.string>`
*   **Request Body:** None
*   **Response Body (Success - 200 OK, List of `SupportTicketResponse`):**
    ```json
    [
        {
            "id": 701,
            "userId": 101,
            "policyId": 202,
            "claimId": null,
            "subject": "Issue with policy document",
            "description": "I cannot find the policy document for my Health Insurance Basic policy.",
            "status": "RESOLVED",
            "response": "We have resent the policy document to your registered email address.",
            "createdAt": "2025-05-26T11:00:00",
            "resolvedAt": "2025-05-26T11:05:00"
        }
        // ... more tickets
    ]
    ```
*   **Example Request (cURL):**
    ```bash
    curl -X GET -H "Authorization: Bearer admin.jwt.token.string" http://localhost:8081/support
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 5. Delete Support Ticket (Admin Only)

*   **Endpoint:** `/support/{ticketId}`
*   **Method:** `DELETE`
*   **Authorization:** Requires ADMIN role.
*   **Request Header:** `Authorization: Bearer <admin.jwt.token.string>`
*   **Request Body:** None
*   **Response Body (Success - 204 No Content):** Empty body.
*   **Response Body (Error - 404 Not Found if ticket doesn't exist)
*   **Example Request (cURL):**
    ```bash
    curl -X DELETE -H "Authorization: Bearer admin.jwt.token.string" http://localhost:8081/support/701
    ```
*   **Example Response (Success):** (Status 204 No Content)


## 6. Deployment

*   **Deployment Environment:**
    *   The [docker-compose.yml](http://_vscodecontentref_/5) facilitates local deployment.
    *   Dockerfile for backend ([Dockerfile](http://_vscodecontentref_/6)) and frontend ([Dockerfile](http://_vscodecontentref_/7)) suggest containerized deployment for other environments (staging, production).
    *   Frontend Docker image uses Nginx to serve static files.
*   **Deployment Steps:**
    *   **Local (using Docker Compose):**
        1.  Ensure Docker and Docker Compose are installed.
        2.  Navigate to the project root directory.
        3.  Run `docker-compose up --build -d`. This will build the images for backend and frontend and start the containers along with the MySQL database.
    *   **Other Environments (General Steps for Containerized Deployment):**
        1.  Build Docker images for backend and frontend:
            ```bash
            docker build -t your-repo/digital-insurance-backend ./backend
            docker build -t your-repo/digital-insurance-frontend ./frontend
            ```
        2.  Push images to a container registry (e.g., Docker Hub, AWS ECR, Google GCR).
        3.  Deploy to a container orchestration platform (e.g., Kubernetes, Docker Swarm, AWS ECS) or a PaaS that supports Docker containers, ensuring the necessary environment variables (like database credentials) are configured.

## 7. Future Enhancements
# Project Documentation

## 1. Project Overview

*   **Project Title:** Digital Insurance Management System
*   **Project Goal:** A Spring Boot application for managing digital insurance policies and user data.
*   **Technologies Used:**
    *   **Backend:**
        *   Spring Boot
        *   Java 17
        *   Spring Data JPA
        *   Spring Web
        *   Spring Security
        *   Lombok
        *   jsonwebtoken (jjwt)
    *   **Frontend:**
        *   Vue.js 3
        *   TypeScript
        *   Vite
        *   Vue Router
        *   Axios
        *   Tailwind CSS
        *   Bootstrap
    *   **Database:**
        *   MySQL (configured via `docker-compose.yml` and `application.properties`)
    *   **Testing:**
        *   JUnit (implied by `spring-boot-starter-test`)
        *   Mockito (based on test file analysis, e.g., `UserPolicyPurchaseServiceTest.java`, `SupportTicketControllerTest.java`)
        *   H2 (for testing, as per `pom.xml`)
    *   **Build Tool:**
        *   Maven (for backend)
        *   npm/vite (for frontend)

## 2. System Architecture

*   **Backend (Spring Boot):**
    *   **REST API Endpoints:**
        *   **Auth Controller (`/auth`):**
            *   `POST /register`: Registers a new user.
            *   `POST /login`: Logs in an existing user.
        *   **Policy Controller (`/policies`):**
            *   `GET /`: Retrieves all available insurance policies.
            *   `GET /{id}`: Retrieves a specific policy by its ID.
            *   `POST /create` (ADMIN): Creates a new insurance policy.
            *   `PUT /update/{id}` (ADMIN): Updates an existing policy.
            *   `DELETE /delete/{id}` (ADMIN): Deletes a policy.
        *   **User Policy Purchase Controller (`/user/policy`):**
            *   `POST /{policyId}/purchase`: Allows a user to purchase a policy.
            *   `GET /`: Retrieves all policies purchased by the current user.
            *   `PUT /`: Updates the status of a user's policy (e.g., to ACTIVE, EXPIRED).
        *   **Policy Renewal Controller:**
            *   `POST /policy/{policyId}/renew`: Renews an existing policy for the user.
            *   `GET /user/policies/renewable`: Retrieves a list of policies that are eligible for renewal for the current user.
        *   **Claim Management Controller:**
            *   `POST /claim`: Submits a new claim for a user's policy.
            *   `GET /user/claim`: Retrieves all claims submitted by all users (ADMIN specific, based on typical design, though controller method name is generic).
            *   `GET /user/claimById`: Retrieves claims for the currently authenticated user.
            *   `PUT /claim/{claimId}/status` (ADMIN): Updates the status of a claim (e.g., PENDING, APPROVED, REJECTED).
            *   `DELETE /claim/{id}` (ADMIN): Deletes a claim.
        *   **Support Ticket Controller (`/support`):**
            *   `POST /`: Creates a new support ticket.
            *   `GET /user`: Retrieves all support tickets for the currently authenticated user.
            *   `PUT /{ticketId}` (ADMIN): Updates a support ticket (e.g., adds a response, changes status).
            *   `GET /` (ADMIN): Retrieves all support tickets in the system.
            *   `DELETE /{ticketId}` (ADMIN): Deletes a support ticket.
    *   **Data Models (Entities):**
        ### User

        | Field    | Type    | Description             |
        |----------|---------|-------------------------|
        | id       | UUID    | Primary key             |
        | name     | String  | User's full name        |
        | email    | String  | User's email address    |
        | password | String  | Hashed password         |
        | phone    | String  | Contact number          |
        | address  | String  | Postal address          |
        | role     | Enum    | USER, ADMIN             |

        ---

        ### Policy

        | Field             | Type     | Description                          |
        |------------------|----------|--------------------------------------|
        | id               | UUID     | Primary key                          |
        | name             | String   | Policy name                          |
        | description      | Text     | Detailed policy description          |
        | premiumAmount    | Decimal  | Premium cost                         |
        | coverageAmount   | Decimal  | Amount covered by policy             |
        | durationMonths   | Integer  | Policy duration in months            |
        | renewalPremiumRate | Decimal| Rate used for renewal calculations   |
        | createdAt        | DateTime | When the policy was added            |
        | category         | Enum     | LIFE, HEALTH, VEHICLE                |

        ---

        ### UserPolicy

        | Field        | Type     | Description                           |
        |-------------|----------|---------------------------------------|
        | id          | UUID     | Primary key                           |
        | user        | FK(User) | Policy owner                          |
        | policy      | FK(Policy)| Purchased policy                      |
        | startDate   | Date     | Start of policy coverage              |
        | endDate     | Date     | End of policy coverage                |
        | status      | Enum     | ACTIVE, EXPIRED, CANCELLED            |
        | premiumPaid | Decimal  | Amount paid by user                   |

        ---

        ### Claim

        | Field          | Type         | Description                          |
        |----------------|--------------|--------------------------------------|
        | id             | UUID         | Primary key                          |
        | userPolicy     | FK(UserPolicy)| Policy on which claim is made       |
        | claimDate      | Date         | When the claim was submitted         |
        | claimAmount    | Decimal      | Requested claim amount               |
        | reason         | Text         | Reason for the claim                 |
        | status         | Enum         | PENDING, APPROVED, REJECTED, PROCESSING |
        | reviewerComment| Text         | Internal review notes                |
        | resolvedDate   | Date         | Date claim was resolved              |

        ---

        ### SupportTicket

        | Field       | Type         | Description                             |
        |-------------|--------------|-----------------------------------------|
        | id          | UUID         | Primary key                             |
        | user        | FK(User)     | User who created the ticket             |
        | policy      | FK(Policy)?  | Related policy (optional)               |
        | claim       | FK(Claim)?   | Related claim (optional)                |
        | subject     | String       | Brief summary of the issue              |
        | description | Text         | Full description of the issue           |
        | status      | Enum         | OPEN, RESOLVED, CLOSED, PENDING_USER_RESPONSE |
        | response    | Text         | Support response                        |
        | createdAt   | DateTime     | When ticket was created                 |
        | resolvedAt  | DateTime     | When ticket was resolved                |

    *   **Services:**
        *   **UserService (`user` package):** Handles user registration, login, and retrieval.
            *   `UserServiceImpl`
        *   **PolicyService (`policy` package):** Manages CRUD operations for insurance policies.
            *   `PolicyServiceImpl`
        *   **UserPolicyPurchaseService (`PolicyPurchaseService` package):** Handles the logic for users purchasing policies and managing their purchased policies.
            *   `UserPolicyPurchaseImpl`
        *   **PolicyRenewalService (`PolicyRenewal` package):** Manages the renewal of policies.
            *   `PolicyRenewalService` (implements `IPolicyRenewalService`)
        *   **ClaimManagementService (`ClaimManagement` package):** Handles submission, retrieval, and status updates for claims.
            *   `ClaimManagementServiceImplementation` (implements `ClaimManagementService`)
        *   **SupportTicketService (`supportTicket` package):** Manages CRUD operations and status updates for support tickets.
            *   `SupportTicketService` (implements `ISupportTicketService`)
        *   **JwtService (`security.jwt` package):** Handles JWT token generation and validation.
    *   **Repositories:**
        *   `UserRepository`: Interacts with the `app_user` table.
        *   `PolicyRepository`: Interacts with the `policy` table.
        *   `UserPolicyRepository`: Interacts with the `user_policy` table.
        *   `ClaimManagementRepository`: Interacts with the `claim` table.
        *   `SupportTicketRepository`: Interacts with the `support_ticket` table.
*   **Frontend (Vue.js):**
    *   **Components:**
        *   `auth/`: Components for login and registration.
        *   `claims/ClaimCard.vue`: Displays individual claim information.
        *   `core/HelloWorld.vue`, `core/NavBar.vue`: Basic UI elements.
        *   `policy/PolicyCard.vue`, `policy/PolicyDetailsModal.vue`, `policy/PurchaseConfirmation.vue`: Components for displaying, detailing, and confirming policy purchases.
        *   `support/AdminClaimsCard.vue`, `support/TicketCard.vue`, `support/TicketDetails.vue`, `support/TicketListItem.vue`: Components for managing support tickets and claims from an admin perspective.
        *   `ui/Button.vue`, `ui/Card.vue`, `ui/ReusableSelect.vue`, `ui/StatsSummary.vue`, `ui/StatusBadge.vue`, `ui/StatusFilter.vue`: Reusable UI elements.
        *   `App.vue`: Root Vue component.
    *   **Routing:**
        *   Managed by `vue-router` (defined in `frontend/src/router.ts`).
        *   `HomePage.vue`: Main landing page.
        *   `NotFoundPage.vue`: For 404 errors.
        *   `UnauthorizedPage.vue`: For access denied errors.
        *   `auth/LoginPage.vue`, `auth/RegisterPage.vue`: User authentication pages.
        *   `claims/AdminClaims.vue`, `claims/ClaimList.vue`, `claims/SubmitClaim.vue`: Pages for managing and submitting claims.
        *   `policy/PolicyCatalog.vue`, `policy/PolicyRenewList.vue`, `policy/RenewPolicyPage.vue`, `policy/UserPolicies.vue`, `policy/UserPurchasedPolicy.vue`: Pages for viewing, purchasing, and managing policies.
        *   `support/AdminTicketList.vue`, `support/SupportForm.vue`, `support/TicketList.vue`: Pages for managing and creating support tickets.
    *   **State Management:**
        *   (No explicit Vuex setup found in `package.json` or file structure. State might be managed via composables or prop drilling.)
    *   **API Integration:**
        *   Uses `axios` for making HTTP requests to the backend (configured in `frontend/src/utils/apis.ts`).
        *   API service functions are defined in `frontend/src/services/api.ts` and `frontend/src/services/auth.ts`.
*   **Database:**
    *   **Schema Design:**
        *   **app_user:** Stores user information (id, name, email, password, phone, address, role).
        *   **policy:** Stores details of insurance policies (id, name, description, premiumAmount, coverageAmount, durationMonths, renewalPremiumRate, createdAt, category).
        *   **user_policy:** Links users to policies they've purchased (id, user_id, policy_id, startDate, endDate, status, premiumPaid).
        *   **claim:** Stores claims made by users (id, user_policy_id, claimDate, claimAmount, reason, status, reviewerComment, resolvedDate).
        *   **support_ticket:** Stores support tickets (id, user_id, policy_id, claim_id, subject, description, status, response, createdAt, resolvedAt).
        *   Relationships are managed via foreign keys (e.g., `user_policy.user_id` references `app_user.id`).
        *   Hibernate (`spring.jpa.hibernate.ddl-auto=update`) manages schema generation based on entity definitions.

## 3. Test Plan

*   **Unit Tests:**
    *   Backend: Testing individual components and functions in isolation (using JUnit and Mockito, with H2 as an in-memory database for tests).
    *   Frontend: (No specific testing framework like Jest or Vitest explicitly configured in `package.json`, but `vue-tsc` is used for type checking).

*   **Test Cases:**

    ## 1. UserServiceTest.java (Service Layer)

    ### `register_shouldCreateUserSuccessfully`
    - **Scenario:** Registering a new user with valid details
    - **Input:** New `User` object with name, email, and password
    - **Expected:** User is saved, role is set to `USER`, password is encoded

    ### `register_shouldThrowException_whenUserAlreadyExists`
    - **Scenario:** Attempting to register a user with an email that already exists
    - **Input:** `User` object with an existing email
    - **Expected:** `UserAlreadyExistException` is thrown

    ### `login_shouldReturnToken_whenCredentialsAreValid`
    - **Scenario:** Logging in with correct email and password
    - **Input:** `User` object with valid email and password
    - **Expected:** JWT token is generated and returned

    ### `login_shouldThrowException_whenUserNotFound`
    - **Scenario:** Attempting to log in with a non-existent email
    - **Input:** `User` object with an email not in the database
    - **Expected:** `InvalidCredentialsException` is thrown

    ### `login_shouldThrowException_whenAuthenticationFails`
    - **Scenario:** Attempting to log in with an existing email but incorrect password
    - **Input:** `User` object with a valid email and wrong password
    - **Expected:** `InvalidCredentialsException` is thrown (due to `BadCredentialsException` from `AuthenticationManager`)

    ### `getAllUsers_shouldReturnAllUsers`
    - **Scenario:** Fetching all registered users
    - **Input:** None
    - **Expected:** A list of all `User` objects

    ### `getUserById_shouldReturnUser_whenUserExists`
    - **Scenario:** Fetching a user by their ID
    - **Input:** Existing user ID
    - **Expected:** The corresponding `User` object

    ### `getUserById_shouldReturnNull_whenUserNotExists`
    - **Scenario:** Fetching a user by a non-existent ID
    - **Input:** Non-existent user ID
    - **Expected:** `null` is returned

    ### `getCurrentEmail_shouldReturnEmail_whenUserDetailsAvailable`
    - **Scenario:** Get current user's email from `SecurityContextHolder` when `UserDetails` is available
    - **Input:** Mocked `SecurityContext` with `UserDetails`
    - **Expected:** User's email string

    ### `getCurrentUserId_shouldReturnUserId_whenTokenAvailable`
    - **Scenario:** Get current user's ID from JWT token in `SecurityContextHolder`
    - **Input:** Mocked `SecurityContext` with JWT in credentials
    - **Expected:** User's ID (Long)

    ### `getUserByEmail_shouldReturnUser_whenUserExists`
    - **Scenario:** Fetching a user by their email
    - **Input:** Existing user email
    - **Expected:** The corresponding `User` object

    ### `convertToDTO_shouldConvertUserToDTO`
    - **Scenario:** Converting a `User` entity to `UserDTO`
    - **Input:** `User` object
    - **Expected:** `UserDTO` with corresponding fields

    ---

    ## 2. UserPolicyPurchaseServiceTest.java (Service Layer)

    ### `purchaseAPolicy_shouldSucceed_whenValidUserAndPolicyAndNotAlreadyPurchased`
    - **Scenario:** A user purchases an available policy
    - **Input:** Valid `policyId`, `userId`. Policy not previously purchased by the user
    - **Expected:** `UserPolicy` object is created with `ACTIVE` status, correct start/end dates, and premium paid

    ### `purchaseAPolicy_shouldFail_whenAlreadyPurchased`
    - **Scenario:** A user attempts to purchase a policy they already own
    - **Input:** `policyId`, `userId` for an already purchased policy
    - **Expected:** `ResourceNotFoundException` (or similar, indicating policy already purchased/not available for purchase)

    ### `purchaseAPolicy_shouldFail_whenPolicyNotFound`
    - **Scenario:** A user attempts to purchase a non-existent policy
    - **Input:** Non-existent `policyId`, valid `userId`
    - **Expected:** `ResourceNotFoundException`

    ### `purchaseAPolicy_shouldFail_whenUserNotFound`
    - **Scenario:** A non-existent user attempts to purchase a policy
    - **Input:** Valid `policyId`, non-existent `userId`
    - **Expected:** `ResourceNotFoundException`

    ### `getPurchasedPolicies_shouldReturnPolicies_whenUserExists`
    - **Scenario:** Fetching all policies purchased by a specific user
    - **Input:** Existing `userId`
    - **Expected:** A list of `UserPolicy` objects belonging to the user

    ### `getPurchasedPolicies_shouldFail_whenUserNotFound`
    - **Scenario:** Fetching policies for a non-existent user
    - **Input:** Non-existent `userId`
    - **Expected:** `ResourceNotFoundException`

    ### `updatePolicy_shouldCancelPolicy`
    - **Scenario:** Cancelling an active user policy
    - **Input:** `userId`, `policyId` of an active policy, new status `CANCELLED`
    - **Expected:** `UserPolicy` status is updated to `CANCELLED`, and `endDate` is set to the current date

    ### `updatePolicy_shouldRenewPolicy`
    - **Scenario:** Renewing a user policy
    - **Input:** `userId`, `policyId`, new status `RENEWED`
    - **Expected:** `UserPolicy` status is updated to `RENEWED` (or `ACTIVE`), and `endDate` is extended based on policy duration

    ### `updatePolicy_shouldFail_whenUserPolicyNotFound`
    - **Scenario:** Attempting to update a non-existent user policy
    - **Input:** Non-existent `userId` or `policyId`
    - **Expected:** `ResourceNotFoundException`

    ---

    ## 3. SupportTicketServiceTest.java (Service Layer)

    ### `createTicket_shouldSetDefaultsAndSave`
    - **Scenario:** Creating a new support ticket with minimal information
    - **Input:** `SupportTicket` object with user, subject, and description
    - **Expected:** Ticket is saved with `OPEN` status and `createdAt` timestamp

    ### `createTicket_shouldPreserveUserAndPolicyData`
    - **Scenario:** Creating a ticket with associated user and policy
    - **Input:** `SupportTicket` with user, policy, subject, description
    - **Expected:** All provided data is preserved, status is `OPEN`

    ### `updateTicket_shouldUpdateAndSave`
    - **Scenario:** Updating an open support ticket to `RESOLVED`
    - **Input:** `ticketId`, response message, new status `RESOLVED`
    - **Expected:** Ticket response and status are updated, `resolvedAt` timestamp is set

    ### `updateTicket_shouldSetResolvedAtWhenStatusIsClosed`
    - **Scenario:** Updating an open support ticket to `CLOSED`
    - **Input:** `ticketId`, response message, new status `CLOSED`
    - **Expected:** Ticket response and status are updated, `resolvedAt` timestamp is set

    ### `updateTicket_shouldThrowNotFound`
    - **Scenario:** Attempting to update a non-existent ticket
    - **Input:** Non-existent `ticketId`
    - **Expected:** `TicketNotFoundException`

    ### `updateTicket_shouldThrowAlreadyClosedException`
    - **Scenario:** Attempting to update a ticket that is already `CLOSED`
    - **Input:** `ticketId` of a closed ticket
    - **Expected:** `TicketAlreadyClosedException`

    ### `getTicketById_shouldReturnTicket`
    - **Scenario:** Fetching an existing ticket by its ID
    - **Input:** Existing `ticketId`
    - **Expected:** The corresponding `SupportTicket` object

    ### `getAllTickets_shouldReturnAllTickets`
    - **Scenario:** Fetching all support tickets
    - **Input:** None
    - **Expected:** A list of all `SupportTicket` objects

    ### `deleteTicket_shouldDeleteSuccessfully`
    - **Scenario:** Deleting an existing ticket
    - **Input:** Existing `ticketId`
    - **Expected:** Ticket is deleted from the repository

    ### `updateTicket_shouldNotAllowClosedTicketReopening`
    - **Scenario:** Attempting to change status of a `CLOSED` ticket to `OPEN`
    - **Input:** `ticketId` of a closed ticket, new status `OPEN`
    - **Expected:** `TicketAlreadyClosedException`

    ### `updateTicket_shouldRejectRedundantResolvedUpdate`
    - **Scenario:** Attempting to update a `RESOLVED` ticket to `RESOLVED` again
    - **Input:** `ticketId` of a resolved ticket, new status `RESOLVED`
    - **Expected:** `InvalidTicketTransitionException`

    ---

    ## 4. PolicyServiceTest.java (Service Layer)

    ### `createPolicy_shouldSavePolicySuccessfully`
    - **Scenario:** Creating a new insurance policy with all details
    - **Input:** `Policy` object with name, description, amounts, duration, rate, category
    - **Expected:** Policy is saved with a generated ID and `createdAt` timestamp

    ### `createPolicy_shouldHandleAllCategories`
    - **Scenario:** Creating policies for different categories (LIFE, HEALTH, VEHICLE)
    - **Input:** `Policy` objects with different categories
    - **Expected:** Policies are saved with their respective categories

    ### `createPolicy_shouldHandleNullValues`
    - **Scenario:** Creating a policy with some fields being null
    - **Input:** `Policy` object with name, other fields null
    - **Expected:** Policy is saved, null fields remain null

    ### `getAllPolicies_shouldReturnAllPolicies`
    - **Scenario:** Fetching all available insurance policies
    - **Input:** None
    - **Expected:** A list of all `Policy` objects

    ### `getAllPolicies_shouldReturnEmptyList_whenNoPolicies`
    - **Scenario:** Fetching policies when none exist
    - **Input:** None
    - **Expected:** An empty list

    > **Note:** Other tests for `getPolicyById`, `updatePolicy`, `deletePolicy` would follow a similar pattern: test success, not found, invalid input scenarios.

    ---

    ## 5. ClaimManagementControllerTest.java (Controller Layer - MockMVC)

    ### `submitClaim_shouldReturnCreatedClaim`
    - **Scenario:** User submits a new claim
    - **Input:** HTTP POST to `/claim` with `UserClaimDTO` (userPolicyId, claimAmount, reason)
    - **Expected:** HTTP 200 OK, response body contains the created `Claim` details

    ### `getAllClaims_shouldReturnListOfClaims`
    - **Scenario:** Admin (or user, depending on endpoint actual role) requests all claims
    - **Input:** HTTP GET to `/user/claim`
    - **Expected:** HTTP 200 OK, response body is a list of `Claim` objects

    ### `getClaimsByUser_shouldReturnUserClaims`
    - **Scenario:** User requests their own claims
    - **Input:** HTTP GET to `/user/claimById` (authenticated user)
    - **Expected:** HTTP 200 OK, response body is a list of `Claim` objects for that user

    ### `updateClaimStatus_shouldReturnUpdatedClaim`
    - **Scenario:** Admin updates the status of a claim
    - **Input:** HTTP PUT to `/claim/{claimId}/status` with `AdminClaimStatusUpdateDTO` (status, reviewerComment)
    - **Expected:** HTTP 200 OK, response body contains the updated `Claim` details

    ### `deleteClaim_shouldDeleteSuccessfully`
    - **Scenario:** Admin deletes a claim
    - **Input:** HTTP DELETE to `/claim/{claimId}`
    - **Expected:** HTTP 200 OK

    ---

    ## 6. PolicyControllerTest.java (Controller Layer - MockMVC)

    ### `getAllPolicies_shouldReturnListOfPolicies`
    - **Scenario:** Requesting all available insurance policies
    - **Input:** HTTP GET to `/policies`
    - **Expected:** HTTP 200 OK, response body is a list of `Policy` objects

    ### `getPolicyById_shouldReturnPolicy_whenPolicyExists`
    - **Scenario:** Requesting a specific policy by its ID
    - **Input:** HTTP GET to `/policies/{id}` with an existing policy ID
    - **Expected:** HTTP 200 OK, response body contains the `Policy` details

    ### `createPolicy_shouldReturnCreatedPolicy`
    - **Scenario:** Admin creates a new policy
    - **Input:** HTTP POST to `/policies/create` with `Policy` data
    - **Expected:** HTTP 201 Created, response body contains the created `Policy` details

    ### `updatePolicy_shouldReturnUpdatedPolicy`
    - **Scenario:** Admin updates an existing policy
    - **Input:** HTTP PUT to `/policies/update/{id}` with updated `Policy` data
    - **Expected:** HTTP 200 OK, response body contains the updated `Policy` details

    ### `deletePolicy_shouldReturnSuccessMessage`
    - **Scenario:** Admin deletes a policy
    - **Input:** HTTP DELETE to `/policies/delete/{id}`
    - **Expected:** HTTP 200 OK, response body is "Policy deleted successfully"

    ---

    ## 7. SupportTicketControllerTest.java (Controller Layer - MockMVC)

    ### `createTicket_shouldReturnCreatedTicket_withNoPolicyOrClaim`
    - **Scenario:** User creates a support ticket without linking to a policy or claim
    - **Input:** HTTP POST to `/support` with `CreateSupportTicketRequest` (subject, description)
    - **Expected:** HTTP 200 OK, response body contains the created `SupportTicket` details

    ### `createTicket_shouldReturnCreatedTicket_withPolicy`
    - **Scenario:** User creates a support ticket linked to a policy
    - **Input:** HTTP POST to `/support` with `CreateSupportTicketRequest` (subject, description, policyId)
    - **Expected:** HTTP 200 OK, response body contains the created `SupportTicket` with policy details

    ### `getTicketsByUser_shouldReturnListOfTickets`
    - **Scenario:** User requests their own support tickets
    - **Input:** HTTP GET to `/support/user` (authenticated user)
    - **Expected:** HTTP 200 OK, response body is a list of `SupportTicket` objects for that user

    ### `updateTicket_shouldReturnUpdatedTicket`
    - **Scenario:** Admin updates a support ticket (e.g., adds response, changes status)
    - **Input:** HTTP PUT to `/support/{ticketId}` with `UpdateSupportTicketRequest` (response, status)
    - **Expected:** HTTP 200 OK, response body contains the updated `SupportTicket` details

    ### `getAllTickets_shouldReturnListOfAllTickets`
    - **Scenario:** Admin requests all support tickets
    - **Input:** HTTP GET to `/support`
    - **Expected:** HTTP 200 OK, response body is a list of all `SupportTicket` objects

    ### `deleteTicket_shouldReturnNoContent`
    - **Scenario:** Admin deletes a support ticket
    - **Input:** HTTP DELETE to `/support/{ticketId}`
    - **Expected:** HTTP 204 No Content

    ---

    ## 8. UserControllerTest.java (Controller Layer - MockMVC)

    ### `register_shouldReturnCreatedUser`
    - **Scenario:** A new user registers
    - **Input:** HTTP POST to `/auth/register` with user details (name, email, password, phone, address)
    - **Expected:** HTTP 201 Created, response body contains `UserDTO` of the created user

    ### `register_shouldHandleUserAlreadyExistsException`
    - **Scenario:** Attempting to register with an email that already exists
    - **Input:** HTTP POST to `/auth/register` with an existing email
    - **Expected:** HTTP 409 Conflict (or appropriate error status based on global exception handler) with message "User already exists"

    ### `login_shouldReturnTokenAndUserDetails`
    - **Scenario:** User logs in with valid credentials
    - **Input:** HTTP POST to `/auth/login` with email and password
    - **Expected:** HTTP 200 OK, response body contains JWT token and `UserDTO`

    ### `login_shouldHandleInvalidCredentialsException`
    - **Scenario:** User logs in with invalid credentials
    - **Input:** HTTP POST to `/auth/login` with incorrect email or password
    - **Expected:** HTTP 401 Unauthorized (or appropriate error status) with message "Invalid credentials"

    ---

    ## 9. UserPolicyPurchaseControllerTest.java (Controller Layer - MockMVC)

    ### `purchasePolicy_shouldReturnCreatedPolicy`
    - **Scenario:** User purchases an insurance policy
    - **Input:** HTTP POST to `/user/policy/{policyId}/purchase` (authenticated user)
    - **Expected:** HTTP 201 Created, response body contains the created `UserPolicy` details

    ### `getUserPolicies_shouldReturnListOfPolicies`
    - **Scenario:** User requests their list of purchased policies
    - **Input:** HTTP GET to `/user/policy` (authenticated user)
    - **Expected:** HTTP 200 OK, response body is a list of `UserPolicy` objects

    ### `updatePolicy_shouldReturnUpdatedPolicy`
    - **Scenario:** User updates the status of one of their policies (e.g., cancels it)
    - **Input:** HTTP PUT to `/user/policy` with query parameters `policyId` and `status`
    - **Expected:** HTTP 200 OK, response body contains the updated `UserPolicy` details

    

    ---

    ## 10. DigitalInsuranceManagementSystemApplicationTests.java

    ### `contextLoads`
    - **Scenario:** Verifies that the Spring application context loads successfully
    - **Input:** None
    - **Expected:** Test passes if the context loads without errors

    ---


    ## 11. ClaimManagementServiceTest.java (Service Layer)

    *(This section needs to be filled in with actual test cases from `ClaimManagementServiceTest.java`)*

    ### `submitClaim_shouldCreateClaimSuccessfully`
    - **Scenario:** A user successfully submits a new claim.
    - **Input:** Valid `UserClaimDTO` (containing userPolicyId, claimAmount, reason).
    - **Expected:** A new `Claim` object is created and saved with status `PENDING`.

    ### `getClaimById_shouldReturnClaim_whenExists`
    - **Scenario:** Retrieving an existing claim by its ID.
    - **Input:** Existing `claimId`.
    - **Expected:** The corresponding `Claim` object.

    ### `getClaimById_shouldThrowException_whenNotExists`
    - **Scenario:** Attempting to retrieve a non-existent claim.
    - **Input:** Non-existent `claimId`.
    - **Expected:** `ResourceNotFoundException`.

    ### `updateClaimStatus_shouldUpdateSuccessfully`
    - **Scenario:** An admin successfully updates the status of a claim.
    - **Input:** `claimId`, new `status` (e.g., APPROVED), `reviewerComment`.
    - **Expected:** The `Claim` object's status, reviewerComment, and resolvedDate are updated.

    ### `updateClaimStatus_shouldThrowException_whenClaimNotExists`
    - **Scenario:** Attempting to update the status of a non-existent claim.
    - **Input:** Non-existent `claimId`, new `status`.
    - **Expected:** `ResourceNotFoundException`.

    ### `deleteClaim_shouldRemoveClaimSuccessfully`
    - **Scenario:** An admin successfully deletes a claim.
    - **Input:** Existing `claimId`.
    - **Expected:** The claim is removed from the repository.

    ---

    ## 12. PolicyRenewalServiceTest.java (Service Layer)

    *(This section needs to be filled in with actual test cases from `PolicyRenewalServiceTest.java`)*

    ### `getRenewablePolicies_shouldReturnEligiblePolicies`
    - **Scenario:** Fetching policies that are eligible for renewal for a user.
    - **Input:** `userId`.
    - **Expected:** A list of `RenewablePolicy` DTOs for policies expiring soon or already expired.

    ### `getRenewablePolicies_shouldReturnEmptyList_whenNoneEligible`
    - **Scenario:** Fetching renewable policies when the user has no policies eligible for renewal.
    - **Input:** `userId`.
    - **Expected:** An empty list.

    ### `getRenewablePolicies_shouldThrowException_whenUserHasNoPolicies`
    - **Scenario:** Fetching renewable policies for a user with no policies at all.
    - **Input:** `userId`.
    - **Expected:** `ResourceNotFoundException`.

    ### `renewPolicy_shouldRenewSuccessfully_whenEligible`
    - **Scenario:** Successfully renewing an eligible user policy.
    - **Input:** `userPolicyId` of an eligible policy.
    - **Expected:** The `UserPolicy` status is set to `ACTIVE` (or remains `ACTIVE`), `startDate` is set to current date, `endDate` is extended, and `premiumPaid` is updated to renewal premium.

    ### `renewPolicy_shouldThrowException_whenPolicyNotFound`
    - **Scenario:** Attempting to renew a non-existent user policy.
    - **Input:** Non-existent `userPolicyId`.
    - **Expected:** `ResourceNotFoundException`.

    ### `renewPolicy_shouldThrowException_whenNotEligibleForRenewalYet`
    - **Scenario:** Attempting to renew a policy that is not yet within the renewal window (e.g., >30 days to expiry).
    - **Input:** `userPolicyId` of a policy not yet eligible.
    - **Expected:** `InvalidPolicyRenewalException`.

    ### `renewPolicy_shouldThrowException_whenMasterPolicyNotFound`
    - **Scenario:** Attempting to renew a user policy whose master policy definition is missing.
    - **Input:** `userPolicyId`.
    - **Expected:** `ResourceNotFoundException` (when trying to fetch the base policy details for renewal).

    ---

    ## 13. PolicyRenewalControllerTest.java (Controller Layer - MockMVC)

    ### `getRenewablePolicies_shouldReturnRenewablePoliciesForCurrentUser_whenAuthenticated`
    - **Scenario:** A logged-in user requests their list of renewable policies.
    - **Input:** HTTP GET to `/user/policies/renewable` (with valid JWT for an authenticated user).
    - **Expected:** HTTP 200 OK, response body is a list of `RenewablePolicyDTO`. Service method `getRenewablePolicies` is called.

    ### `getRenewablePolicies_shouldReturnEmptyList_whenNoPoliciesAreRenewable`
    - **Scenario:** A logged-in user requests renewable policies, but the service returns an empty list.
    - **Input:** HTTP GET to `/user/policies/renewable` (authenticated user).
    - **Expected:** HTTP 200 OK, response body is an empty list.

    ### `getRenewablePolicies_shouldReturnUnauthorized_whenNotAuthenticated`
    - **Scenario:** An unauthenticated request is made to get renewable policies.
    - **Input:** HTTP GET to `/user/policies/renewable` (without or with invalid JWT).
    - **Expected:** HTTP 401 Unauthorized.

    ### `renewPolicy_shouldSuccessfullyRenewPolicy_whenAuthenticatedAndEligible`
    - **Scenario:** A logged-in user successfully renews one of their eligible policies.
    - **Input:** HTTP POST to `/policy/{userPolicyId}/renew` (with valid JWT, valid and eligible `userPolicyId`).
    - **Expected:** HTTP 200 OK, response body contains the updated `UserPolicyDTO`. Service method `renewPolicy` is called.

    ### `renewPolicy_shouldReturnNotFound_whenUserPolicyDoesNotExist`
    - **Scenario:** A user attempts to renew a `UserPolicy` that does not exist (service throws `ResourceNotFoundException`).
    - **Input:** HTTP POST to `/policy/{userPolicyId}/renew` with a non-existent `userPolicyId`.
    - **Expected:** HTTP 404 Not Found.

    ### `renewPolicy_shouldReturnBadRequest_whenPolicyNotEligibleForRenewal`
    - **Scenario:** A user attempts to renew a policy that is not eligible (service throws `InvalidPolicyRenewalException`).
    - **Input:** HTTP POST to `/policy/{userPolicyId}/renew` for a non-eligible policy.
    - **Expected:** HTTP 400 Bad Request.

    ### `renewPolicy_shouldReturnUnauthorized_whenNotAuthenticated`
    - **Scenario:** An unauthenticated request is made to renew a policy.
    - **Input:** HTTP POST to `/policy/{userPolicyId}/renew`.
    - **Expected:** HTTP 401 Unauthorized.

    ## 14. Repository Layer Tests (@DataJpaTest)

    *(This section details tests for custom query methods and specific repository behaviors beyond standard Spring Data JPA CRUD, using H2 in-memory database.)*

    ### `ClaimManagementRepositoryTest.java`
    - **`findByUserPolicy_UserId_shouldReturnClaimsForUser`**
        - **Scenario:** Test fetching claims associated with a specific user ID via their user policies.
        - **Input:** `userId`.
        - **Expected:** Returns a list of `Claim` entities linked to the given user.
    - **`findByStatus_shouldReturnClaimsWithStatus`**
        - **Scenario:** Test fetching claims by a specific `ClaimStatus`.
        - **Input:** `ClaimStatus` (e.g., PENDING, APPROVED).
        - **Expected:** Returns a list of `Claim` entities matching the status.
    - **(Other custom queries if any)**

    ### `SupportTicketRepositoryTest.java`
    - **`findByUserIdAndStatus_shouldReturnUserTicketsWithStatus`**
        - **Scenario:** Test fetching support tickets for a specific user filtered by status.
        - **Input:** `userId`, `SupportTicketStatus`.
        - **Expected:** Returns a list of `SupportTicket` entities matching the criteria.
    - **`findByPolicyId_shouldReturnTicketsForPolicy`**
        - **Scenario:** Test fetching support tickets related to a specific master policy ID.
        - **Input:** `policyId`.
        - **Expected:** Returns a list of `SupportTicket` entities linked to that policy.
    - **(Other custom queries if any)**

    ### `UserPolicyRepositoryTest.java`
    - **`findByUserIdAndPolicyId_shouldReturnSpecificUserPolicy`**
        - **Scenario:** Test fetching a specific policy purchased by a specific user.
        - **Input:** `userId`, `policyId` (master policy ID).
        - **Expected:** Returns an `Optional<UserPolicy>` for the matching record.
    - **`findByUserId_shouldReturnAllUserPolicies`**
        - **Scenario:** Test fetching all policies purchased by a user.
        - **Input:** `userId`.
        - **Expected:** Returns a list of `UserPolicy` entities for the user.
    - **`findByEndDateBeforeAndStatus_shouldReturnExpiringOrExpiredPoliciesWithStatus`**
        - **Scenario:** Test fetching policies that ended before a certain date and have a specific status (e.g., for identifying policies that expired and were not renewed).
        - **Input:** `LocalDate`, `PolicyStatus`.
        - **Expected:** Returns a list of `UserPolicy` entities matching the criteria.
    - **`findRenewablePoliciesByUserId_shouldReturnPoliciesEligibleForRenewal`** (if such a custom query exists for renewal eligibility)
        - **Scenario:** Test fetching user policies that are within the renewal window (e.g., endDate is near or past, and status is ACTIVE or RENEWABLE).
        - **Input:** `userId`, current date.
        - **Expected:** Returns a list of `UserPolicy` entities eligible for renewal.
    - **(Other custom queries if any)**

## 4. Setup and Configuration

*   **Backend Setup:**
    *   **Prerequisites:**
        *   Java 17
        *   Maven
    *   **Steps to run the backend:**
        ```bash
        cd backend
        ./mvnw spring-boot:run
        ```
*   **Frontend Setup:**
    *   **Prerequisites:**
        *   Node.js
        *   npm
    *   **Steps to run the frontend:**
        ```bash
        cd frontend
        npm install
        npm run dev 
        ```
        (Note: `npm run serve` is often used with Vue CLI, but Vite uses `npm run dev` by default as seen in [package.json](http://_vscodecontentref_/0))
*   **Database Setup:**
    *   The application uses MySQL, which is configured to run in a Docker container via [docker-compose.yml](http://_vscodecontentref_/1).
    *   **Steps to run the database (and other services via Docker Compose):**
        ```bash
        docker-compose up -d mysql-docker # To start only MySQL
        # or to start all services defined in docker-compose.yml
        docker-compose up -d
        ```
    *   **Database Connection Details (from [application.properties](http://_vscodecontentref_/2) and [docker-compose.yml](http://_vscodecontentref_/3)):**
        *   Driver: `com.mysql.cj.jdbc.Driver`
        *   URL: `jdbc:mysql://mysql-docker:3306/DIMS` (when running with Docker Compose) or `${DATABASE_URL}` (can be set as environment variable)
        *   Username: `root` (for Docker Compose) or `${DATABASE_USER}`
        *   Password: `12345` (for Docker Compose) or `${DATABASE_PASSWORD}`
        *   The backend service in [docker-compose.yml](http://_vscodecontentref_/4) is configured to connect to `mysql-docker` on port `3306` with database `DIMS`, user `root`, and password `12345`.

## 5. API Documentation

### Authentication Endpoints (`/auth`)

#### 1. Register User

*   **Endpoint:** `/auth/register`
*   **Method:** `POST`
*   **Request Body:**
    ```json
    {
      "name": "John Doe",
      "email": "john.doe@example.com",
      "password": "password123",
      "phone": "1234567890",
      "address": "123 Main St"
    }
    ```
*   **Response Body (Success - 201 Created):**
    ```json
    {
      "id": 1,
      "name": "John Doe",
      "email": "john.doe@example.com",
      "role": "USER"
    }
    ```
*   **Response Body (Error - 409 Conflict):**
    ```text
    User already exists
    ```
*   **Example Request (cURL):**
    ```bash
    curl -X POST -H "Content-Type: application/json" -d '{
      "name": "John Doe",
      "email": "john.doe@example.com",
      "password": "password123",
      "phone": "1234567890",
      "address": "123 Main St"
    }' http://localhost:8081/auth/register
    ```
*   **Example Response (Success):**
    ```json
    {
      "id": 1,
      "name": "John Doe",
      "email": "john.doe@example.com",
      "role": "USER"
    }
    ```

#### 2. Login User

*   **Endpoint:** `/auth/login`
*   **Method:** `POST`
*   **Request Body:**
    ```json
    {
      "email": "john.doe@example.com",
      "password": "password123"
    }
    ```
*   **Response Body (Success - 200 OK):**
    ```json
    {
      "token": "jwt.token.string",
      "user": {
        "id": 1,
        "name": "John Doe",
        "email": "john.doe@example.com",
        "role": "USER"
      }
    }
    ```
*   **Response Body (Error - 401 Unauthorized):**
    ```text
    Invalid credentials
    ```
*   **Example Request (cURL):**
    ```bash
    curl -X POST -H "Content-Type: application/json" -d '{
      "email": "john.doe@example.com",
      "password": "password123"
    }' http://localhost:8081/auth/login
    ```
*   **Example Response (Success):**
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInJvbGUiOiJVU0VSIiwic3ViIjoiam9obi5kb2VAZXhhbXBsZS5jb20iLCJpYXQiOjE2NzgzNzQwNzYsImV4cCI6MTY3ODM3NzY3Nn0.exampleToken",
      "user": {
        "id": 1,
        "name": "John Doe",
        "email": "john.doe@example.com",
        "role": "USER"
      }
    }
    ```

### Policy Endpoints (`/policies`)

#### 1. Get All Policies

*   **Endpoint:** `/policies`
*   **Method:** `GET`
*   **Request Body:** None
*   **Response Body (Success - 200 OK):**
    ```json
    [
      {
        "id": 1,
        "name": "Life Insurance Premium",
        "description": "Comprehensive life insurance coverage",
        "premiumAmount": 500.00,
        "coverageAmount": 100000.00,
        "durationMonths": 12,
        "renewalPremiumRate": 0.05,
        "createdAt": "2025-05-26T10:00:00",
        "category": "LIFE"
      },
      {
        "id": 2,
        "name": "Health Insurance Basic",
        "description": "Basic health insurance coverage",
        "premiumAmount": 300.00,
        "coverageAmount": 50000.00,
        "durationMonths": 12,
        "renewalPremiumRate": 0.03,
        "createdAt": "2025-05-26T10:05:00",
        "category": "HEALTH"
      }
    ]
    ```
*   **Example Request (cURL):**
    ```bash
    curl -X GET -H "Authorization: Bearer jwt.token.string" http://localhost:8081/policies
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 2. Get Policy By ID

*   **Endpoint:** `/policies/{id}`
*   **Method:** `GET`
*   **Request Body:** None
*   **Response Body (Success - 200 OK):**
    ```json
    {
      "id": 1,
      "name": "Life Insurance Premium",
      "description": "Comprehensive life insurance coverage",
      "premiumAmount": 500.00,
      "coverageAmount": 100000.00,
      "durationMonths": 12,
      "renewalPremiumRate": 0.05,
      "createdAt": "2025-05-26T10:00:00",
      "category": "LIFE"
    }
    ```
*   **Response Body (Error - 404 Not Found if policy doesn't exist, or 200 OK with null/empty body if service returns null):** (Behavior might vary based on service implementation for not found)
*   **Example Request (cURL):**
    ```bash
    curl -X GET -H "Authorization: Bearer jwt.token.string" http://localhost:8081/policies/1
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 3. Create Policy (Admin Only)

*   **Endpoint:** `/policies/create`
*   **Method:** `POST`
*   **Authorization:** Requires ADMIN role.
*   **Request Body:**
    ```json
    {
      "name": "Vehicle Insurance Gold",
      "description": "Full coverage for vehicles",
      "premiumAmount": 750.00,
      "coverageAmount": 25000.00,
      "durationMonths": 12,
      "renewalPremiumRate": 0.06,
      "category": "VEHICLE"
    }
    ```
*   **Response Body (Success - 201 Created):**
    ```json
    {
      "id": 3,
      "name": "Vehicle Insurance Gold",
      "description": "Full coverage for vehicles",
      "premiumAmount": 750.00,
      "coverageAmount": 25000.00,
      "durationMonths": 12,
      "renewalPremiumRate": 0.06,
      "createdAt": "2025-05-26T10:10:00",
      "category": "VEHICLE"
    }
    ```
*   **Example Request (cURL):**
    ```bash
    curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer admin.jwt.token.string" -d '{
      "name": "Vehicle Insurance Gold",
      "description": "Full coverage for vehicles",
      "premiumAmount": 750.00,
      "coverageAmount": 25000.00,
      "durationMonths": 12,
      "renewalPremiumRate": 0.06,
      "category": "VEHICLE"
    }' http://localhost:8081/policies/create
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 4. Update Policy (Admin Only)

*   **Endpoint:** `/policies/update/{id}`
*   **Method:** `PUT`
*   **Authorization:** Requires ADMIN role.
*   **Request Body:**
    ```json
    {
      "name": "Life Insurance Premium Plus",
      "description": "Enhanced life insurance coverage",
      "premiumAmount": 550.00,
      "coverageAmount": 120000.00,
      "durationMonths": 12,
      "renewalPremiumRate": 0.055,
      "category": "LIFE"
    }
    ```
*   **Response Body (Success - 200 OK):**
    ```json
    {
      "id": 1,
      "name": "Life Insurance Premium Plus",
      "description": "Enhanced life insurance coverage",
      "premiumAmount": 550.00,
      "coverageAmount": 120000.00,
      "durationMonths": 12,
      "renewalPremiumRate": 0.055,
      "createdAt": "2025-05-26T10:00:00", 
      "category": "LIFE"
    }
    ```
*   **Response Body** (Error - 404 Not Found if policy doesn't exist)
*   **Example Request (cURL):**
    ```bash
    curl -X PUT -H "Content-Type: application/json" -H "Authorization: Bearer admin.jwt.token.string" -d '{
      "name": "Life Insurance Premium Plus",
      "description": "Enhanced life insurance coverage",
      "premiumAmount": 550.00,
      "coverageAmount": 120000.00,
      "durationMonths": 12,
      "renewalPremiumRate": 0.055,
      "category": "LIFE"
    }' http://localhost:8081/policies/update/1
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 5. Delete Policy (Admin Only)

*   **Endpoint:** `/policies/delete/{id}`
*   **Method:** `DELETE`
*   **Authorization:** Requires ADMIN role.
*   **Request Body:** None
*   **Response Body (Success - 200 OK):**
    ```text
    Policy deleted successfully
    ```
*   **Response Body** (Error - 404 Not Found if policy doesn't exist, or if service throws an exception for deletion failure)
*   **Example Request (cURL):**
    ```bash
    curl -X DELETE -H "Authorization: Bearer admin.jwt.token.string" http://localhost:8081/policies/delete/1
    ```
*   **Example Response (Success):**
    ```text
    Policy deleted successfully
    ```

### User Policy Purchase Endpoints (`/user/policy`)

#### 1. Purchase a Policy

*   **Endpoint:** `/user/policy/{policyId}/purchase`
*   **Method:** `POST`
*   **Request Header:** `Authorization: Bearer <user.jwt.token.string>`
*   **Request Body:** None
*   **Response Body (Success - 201 Created):**
    ```json
    {
        "id": 1,
        "user": {
            "id": 101,
            "name": "Current User",
            "email": "current.user@example.com",
            "role": "USER"
            // other user fields excluded for brevity
        },
        "policy": {
            "id": 202,
            "name": "Health Insurance Basic",
            "description": "Basic health insurance coverage",
            "premiumAmount": 300.00,
            "coverageAmount": 50000.00,
            "durationMonths": 12,
            "renewalPremiumRate": 0.03,
            "createdAt": "2025-05-26T10:05:00",
            "category": "HEALTH"
        },
        "startDate": "2025-05-26",
        "endDate": "2026-05-26",
        "status": "ACTIVE",
        "premiumPaid": 300.00
    }
    ```
*   **Response Body (Error - 404 Not Found if policy or user doesn't exist, 400 Bad Request for other issues like already purchased)
*   **Example Request (cURL):**
    ```bash
    curl -X POST -H "Authorization: Bearer user.jwt.token.string" http://localhost:8081/user/policy/202/purchase
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 2. Get User's Purchased Policies

*   **Endpoint:** `/user/policy`
*   **Method:** `GET`
*   **Request Header:** `Authorization: Bearer <user.jwt.token.string>`
*   **Request Body:** None
*   **Response Body (Success - 200 OK):**
    ```json
    [
        {
            "id": 1,
            "user": { "id": 101, "name": "Current User", "email": "current.user@example.com", "role": "USER" },
            "policy": { "id": 202, "name": "Health Insurance Basic", "category": "HEALTH", "premiumAmount": 300.00 },
            "startDate": "2025-05-26",
            "endDate": "2026-05-26",
            "status": "ACTIVE",
            "premiumPaid": 300.00
        }
        // ... more user policies
    ]
    ```
*   **Example Request (cURL):**
    ```bash
    curl -X GET -H "Authorization: Bearer user.jwt.token.string" http://localhost:8081/user/policy
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 3. Update User Policy Status (e.g., Cancel)

*   **Endpoint:** `/user/policy`
*   **Method:** `PUT`
*   **Request Header:** `Authorization: Bearer <user.jwt.token.string>`
*   **Request Parameters:** `policyId={userPolicyId}&status={newStatus}` (e.g., `policyId=1&status=CANCELLED`)
*   **Request Body:** None
*   **Response Body (Success - 200 OK):**
    ```json
    {
        "id": 1,
        "user": { "id": 101, "name": "Current User", "email": "current.user@example.com", "role": "USER" },
        "policy": { "id": 202, "name": "Health Insurance Basic", "category": "HEALTH", "premiumAmount": 300.00 },
        "startDate": "2025-05-26",
        "endDate": "2026-05-26",
        "status": "CANCELLED", // Updated status
        "premiumPaid": 300.00
    }
    ```
*   **Response Body** (Error - 404 Not Found if user policy doesn't exist, 400 Bad Request for invalid status or other issues)
*   **Example Request (cURL):**
    ```bash
    curl -X PUT -H "Authorization: Bearer user.jwt.token.string" "http://localhost:8081/user/policy?policyId=1&status=CANCELLED"
    ```
*   **Example Response (Success):** (As shown in Response Body)

### Policy Renewal Endpoints

#### 1. Renew a Policy

*   **Endpoint:** `/policy/{policyId}/renew` (Note: `policyId` here refers to the `UserPolicy` ID)
*   **Method:** `POST`
*   **Request Header:** `Authorization: Bearer <user.jwt.token.string>` (Implicitly uses current user from token)
*   **Request Body:** None
*   **Response Body (Success - 200 OK):**
    ```json
    {
        "id": 1, // UserPolicy ID
        "user": { "id": 101, "name": "Current User", "email": "current.user@example.com", "role": "USER" },
        "policy": { "id": 202, "name": "Health Insurance Basic", "durationMonths": 12, "renewalPremiumRate": 330.00 }, // renewalPremiumRate is the new premium
        "startDate": "2025-05-26", // New start date
        "endDate": "2026-05-26",   // New end date
        "status": "ACTIVE", // Or RENEWED, depending on implementation
        "premiumPaid": 330.00 // New premium paid
    }
    ```
*   **Response Body (Error - 404 Not Found if UserPolicy doesn't exist, 400 Bad Request if not eligible for renewal)
*   **Example Request (cURL):**
    ```bash
    curl -X POST -H "Authorization: Bearer user.jwt.token.string" http://localhost:8081/policy/1/renew
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 2. Get Renewable Policies for Current User

*   **Endpoint:** `/user/policies/renewable`
*   **Method:** `GET`
*   **Request Header:** `Authorization: Bearer <user.jwt.token.string>`
*   **Request Body:** None
*   **Response Body (Success - 200 OK):**
    ```json
    [
        {
            "userPolicyId": 1,
            "policyName": "Health Insurance Basic",
            "endDate": "2025-06-15", // Expiring soon or expired
            "premiumPaid": 300.00, // Original premium
            "renewalPremiumRate": 330.00 // New premium for renewal
        }
        // ... more renewable policies
    ]
    ```
*   **Example Request (cURL):**
    ```bash
    curl -X GET -H "Authorization: Bearer user.jwt.token.string" http://localhost:8081/user/policies/renewable
    ```
*   **Example Response (Success):** (As shown in Response Body)

### Claim Management Endpoints

#### 1. Submit a Claim

*   **Endpoint:** `/claim`
*   **Method:** `POST`
*   **Request Header:** `Authorization: Bearer <user.jwt.token.string>` (User identified by token)
*   **Request Body (`UserClaimDTO`):**
    ```json
    {
        "userPolicyId": 1, // ID of the UserPolicy being claimed against
        "claimAmount": 150.75,
        "reason": "Hospital visit for consultation"
    }
    ```
*   **Response Body (Success - 200 OK):**
    ```json
    {
        "id": 501,
        "userPolicy": {
            "id": 1,
            "user": { "id": 101, "name": "Current User" },
            "policy": { "id": 202, "name": "Health Insurance Basic" }
            // ... other UserPolicy fields
        },
        "claimDate": "2025-05-26",
        "claimAmount": 150.75,
        "reason": "Hospital visit for consultation",
        "status": "PENDING",
        "reviewerComment": null,
        "resolvedDate": null
    }
    ```
*   **Response Body (Error - 404 Not Found if UserPolicy doesn't exist or doesn't belong to user, 400 Bad Request for invalid data)
*   **Example Request (cURL):**
    ```bash
    curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer user.jwt.token.string" -d '{
        "userPolicyId": 1,
        "claimAmount": 150.75,
        "reason": "Hospital visit for consultation"
    }' http://localhost:8081/claim
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 2. Get All Claims (Admin View - Potentially)

*   **Endpoint:** `/user/claim` (Endpoint name might be misleading if it's for admins)
*   **Method:** `GET`
*   **Request Header:** `Authorization: Bearer <admin.jwt.token.string>` (If admin restricted)
*   **Request Body:** None
*   **Response Body (Success - 200 OK):**
    ```json
    [
        {
            "id": 501,
            "userPolicy": { "id": 1, "user": { "id": 101 }, "policy": { "id": 202 } },
            "claimDate": "2025-05-26",
            "claimAmount": 150.75,
            "reason": "Hospital visit for consultation",
            "status": "PENDING",
            "reviewerComment": null,
            "resolvedDate": null
        }
        // ... more claims
    ]
    ```
*   **Example Request (cURL):**
    ```bash
    curl -X GET -H "Authorization: Bearer admin.jwt.token.string" http://localhost:8081/user/claim
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 3. Get Claims by Current User

*   **Endpoint:** `/user/claimById`
*   **Method:** `GET`
*   **Request Header:** `Authorization: Bearer <user.jwt.token.string>`
*   **Request Body:** None
*   **Response Body (Success - 200 OK):**
    ```json
    [
        {
            "id": 501,
            "userPolicy": { "id": 1, "user": { "id": 101 }, "policy": { "id": 202 } },
            "claimDate": "2025-05-26",
            "claimAmount": 150.75,
            "reason": "Hospital visit for consultation",
            "status": "PENDING",
            "reviewerComment": null,
            "resolvedDate": null
        }
        // ... more claims for the current user
    ]
    ```
*   **Example Request (cURL):**
    ```bash
    curl -X GET -H "Authorization: Bearer user.jwt.token.string" http://localhost:8081/user/claimById
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 4. Update Claim Status (Admin Only)

*   **Endpoint:** `/claim/{claimId}/status`
*   **Method:** `PUT`
*   **Authorization:** Requires ADMIN role.
*   **Request Header:** `Authorization: Bearer <admin.jwt.token.string>`
*   **Request Body (`AdminClaimStatusUpdateDTO`):**
    ```json
    {
        "status": "APPROVED", // e.g., PENDING, APPROVED, REJECTED, PROCESSING
        "reviewerComment": "Claim approved as per policy terms."
    }
    ```
*   **Response Body (Success - 200 OK):**
    ```json
    {
        "id": 501,
        "userPolicy": { "id": 1, "user": { "id": 101 }, "policy": { "id": 202 } },
        "claimDate": "2025-05-26",
        "claimAmount": 150.75,
        "reason": "Hospital visit for consultation",
        "status": "APPROVED", // Updated status
        "reviewerComment": "Claim approved as per policy terms.",
        "resolvedDate": "2025-05-27" // Or current date when resolved
    }
    ```
*   **Response Body (Error - 404 Not Found if claim doesn't exist, 400 Bad Request for invalid status)
*   **Example Request (cURL):**
    ```bash
    curl -X PUT -H "Content-Type: application/json" -H "Authorization: Bearer admin.jwt.token.string" -d '{
        "status": "APPROVED",
        "reviewerComment": "Claim approved as per policy terms."
    }' http://localhost:8081/claim/501/status
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 5. Delete Claim (Admin Only)

*   **Endpoint:** `/claim/{id}`
*   **Method:** `DELETE`
*   **Authorization:** Requires ADMIN role.
*   **Request Header:** `Authorization: Bearer <admin.jwt.token.string>`
*   **Request Body:** None
*   **Response Body (Success - 200 OK):** Empty body or a success message (controller returns `ResponseEntity.ok().build()` which is empty).
*   **Response Body (Error - 404 Not Found if claim doesn't exist)
*   **Example Request (cURL):**
    ```bash
    curl -X DELETE -H "Authorization: Bearer admin.jwt.token.string" http://localhost:8081/claim/501
    ```
*   **Example Response (Success):** (Status 200 OK with no content)

### Support Ticket Endpoints (`/support`)

#### 1. Create Support Ticket

*   **Endpoint:** `/support`
*   **Method:** `POST`
*   **Request Header:** `Authorization: Bearer <user.jwt.token.string>`
*   **Request Body (`CreateSupportTicketRequest`):**
    ```json
    {
        "subject": "Issue with policy document",
        "description": "I cannot find the policy document for my Health Insurance Basic policy.",
        "policyId": 202, // Optional: ID of the master Policy
        "claimId": null   // Optional: ID of the Claim
    }
    ```
*   **Response Body (Success - 200 OK, `SupportTicketResponse`):**
    ```json
    {
        "id": 701,
        "userId": 101,
        "policyId": 202,
        "claimId": null,
        "subject": "Issue with policy document",
        "description": "I cannot find the policy document for my Health Insurance Basic policy.",
        "status": "OPEN",
        "response": null,
        "createdAt": "2025-05-26T11:00:00",
        "resolvedAt": null
    }
    ```
*   **Response Body (Error - 403 Forbidden if policy/claim does not belong to user, 404 Not Found if user/policy/claim not found)
*   **Example Request (cURL):**
    ```bash
    curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer user.jwt.token.string" -d '{
        "subject": "Issue with policy document",
        "description": "I cannot find the policy document for my Health Insurance Basic policy.",
        "policyId": 202
    }' http://localhost:8081/support
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 2. Get Support Tickets for Current User

*   **Endpoint:** `/support/user`
*   **Method:** `GET`
*   **Request Header:** `Authorization: Bearer <user.jwt.token.string>`
*   **Request Body:** None
*   **Response Body (Success - 200 OK, List of `SupportTicketResponse`):**
    ```json
    [
        {
            "id": 701,
            "userId": 101,
            "policyId": 202,
            "claimId": null,
            "subject": "Issue with policy document",
            "description": "I cannot find the policy document for my Health Insurance Basic policy.",
            "status": "OPEN",
            "response": null,
            "createdAt": "2025-05-26T11:00:00",
            "resolvedAt": null
        }
        // ... more tickets for the user
    ]
    ```
*   **Example Request (cURL):**
    ```bash
    curl -X GET -H "Authorization: Bearer user.jwt.token.string" http://localhost:8081/support/user
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 3. Update Support Ticket (Admin Only)

*   **Endpoint:** `/support/{ticketId}`
*   **Method:** `PUT`
*   **Authorization:** Requires ADMIN role.
*   **Request Header:** `Authorization: Bearer <admin.jwt.token.string>`
*   **Request Body (`UpdateSupportTicketRequest`):**
    ```json
    {
        "response": "We have resent the policy document to your registered email address.",
        "status": "RESOLVED" // e.g., OPEN, RESOLVED, CLOSED, PENDING_USER_RESPONSE
    }
    ```
*   **Response Body (Success - 200 OK, `SupportTicketResponse`):**
    ```json
    {
        "id": 701,
        "userId": 101,
        "policyId": 202,
        "claimId": null,
        "subject": "Issue with policy document",
        "description": "I cannot find the policy document for my Health Insurance Basic policy.",
        "status": "RESOLVED",
        "response": "We have resent the policy document to your registered email address.",
        "createdAt": "2025-05-26T11:00:00",
        "resolvedAt": "2025-05-26T11:05:00"
    }
    ```
*   **Response Body (Error - 404 Not Found if ticket doesn't exist, 400 Bad Request for invalid status or other issues)
*   **Example Request (cURL):**
    ```bash
    curl -X PUT -H "Content-Type: application/json" -H "Authorization: Bearer admin.jwt.token.string" -d '{
        "response": "We have resent the policy document to your registered email address.",
        "status": "RESOLVED"
    }' http://localhost:8081/support/701
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 4. Get All Support Tickets (Admin Only)

*   **Endpoint:** `/support`
*   **Method:** `GET`
*   **Authorization:** Requires ADMIN role.
*   **Request Header:** `Authorization: Bearer <admin.jwt.token.string>`
*   **Request Body:** None
*   **Response Body (Success - 200 OK, List of `SupportTicketResponse`):**
    ```json
    [
        {
            "id": 701,
            "userId": 101,
            "policyId": 202,
            "claimId": null,
            "subject": "Issue with policy document",
            "description": "I cannot find the policy document for my Health Insurance Basic policy.",
            "status": "RESOLVED",
            "response": "We have resent the policy document to your registered email address.",
            "createdAt": "2025-05-26T11:00:00",
            "resolvedAt": "2025-05-26T11:05:00"
        }
        // ... more tickets
    ]
    ```
*   **Example Request (cURL):**
    ```bash
    curl -X GET -H "Authorization: Bearer admin.jwt.token.string" http://localhost:8081/support
    ```
*   **Example Response (Success):** (As shown in Response Body)

#### 5. Delete Support Ticket (Admin Only)

*   **Endpoint:** `/support/{ticketId}`
*   **Method:** `DELETE`
*   **Authorization:** Requires ADMIN role.
*   **Request Header:** `Authorization: Bearer <admin.jwt.token.string>`
*   **Request Body:** None
*   **Response Body (Success - 204 No Content):** Empty body.
*   **Response Body (Error - 404 Not Found if ticket doesn't exist)
*   **Example Request (cURL):**
    ```bash
    curl -X DELETE -H "Authorization: Bearer admin.jwt.token.string" http://localhost:8081/support/701
    ```
*   **Example Response (Success):** (Status 204 No Content)


## 6. Deployment

*   **Deployment Environment:**
    *   The [docker-compose.yml](http://_vscodecontentref_/5) facilitates local deployment.
    *   Dockerfile for backend ([Dockerfile](http://_vscodecontentref_/6)) and frontend ([Dockerfile](http://_vscodecontentref_/7)) suggest containerized deployment for other environments (staging, production).
    *   Frontend Docker image uses Nginx to serve static files.
*   **Deployment Steps:**
    *   **Local (using Docker Compose):**
        1.  Ensure Docker and Docker Compose are installed.
        2.  Navigate to the project root directory.
        3.  Run `docker-compose up --build -d`. This will build the images for backend and frontend and start the containers along with the MySQL database.
    *   **Other Environments (General Steps for Containerized Deployment):**
        1.  Build Docker images for backend and frontend:
            ```bash
            docker build -t your-repo/digital-insurance-backend ./backend
            docker build -t your-repo/digital-insurance-frontend ./frontend
            ```
        2.  Push images to a container registry (e.g., Docker Hub, AWS ECR, Google GCR).
        3.  Deploy to a container orchestration platform (e.g., Kubernetes, Docker Swarm, AWS ECS) or a PaaS that supports Docker containers, ensuring the necessary environment variables (like database credentials) are configured.

## 7. Future Enhancements

*   Implement two-factor authentication.
*   Integrate with payment gateways.
*   Automated claim processing.
*   Personalized policy recommendations.
*   Chatbot for customer support.
*   Integration with third-party services for data verification (e.g., vehicle history).

## 8. Team and Roles

- **Sachin Gagneja** – Full Stack Developer  
  Developed the end-to-end project for **Module 1**.

- **Bhawna Jain** – Full Stack Developer  
  Developed the end-to-end project for **Module 2**.

- **Harshitha Kolipaka** – Full Stack Developer  
  Developed the end-to-end project for **Module 3**.

- **Muneer Ahmed** – Full Stack Developer  
  Developed the end-to-end project for **Module 4**.

- **Nikhil Sharma** – Full Stack Developer  
  Developed the end-to-end project for **Module 5**.



*   Implement two-factor authentication.
*   Integrate with payment gateways.
*   Automated claim processing.
*   Personalized policy recommendations.
*   Chatbot for customer support.
*   Integration with third-party services for data verification (e.g., vehicle history).

## 8. Team and Roles

- **Sachin Gagneja** – Full Stack Developer  
  Developed the end-to-end project for **Module 1**.

- **Bhawna Jain** – Full Stack Developer  
  Developed the end-to-end project for **Module 2**.

- **Harshitha Kolipaka** – Full Stack Developer  
  Developed the end-to-end project for **Module 3**.

- **Muneer Ahmed** – Full Stack Developer  
  Developed the end-to-end project for **Module 4**.

- **Nikhil Sharma** – Full Stack Developer  
  Developed the end-to-end project for **Module 5**.


