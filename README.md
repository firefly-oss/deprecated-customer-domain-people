# customer-domain-people

Initial structure with a CQRS orientation (only CQ implemented for now) for the People domain.

## Modules
- customer-domain-people-interfaces: DTOs and shared contracts.
  - Command DTOs (input): e.g., `RegisterCustomerCommand`.
  - Query DTOs (output): e.g., `PersonView` (consolidated customer profile).
- customer-domain-people-core: Domain services and clients.
  - `PersonQueryService`: query interface.
  - `PersonCommandService`: registration interface.
  - `MockPersonClient`: mock client that returns in-memory data.
  - `PersonQueryServiceImpl`: implementation that delegates to the client.
- customer-domain-people-web: Web layer (Spring Boot WebFlux).
  - `PeopleController`: endpoints for customers.
- customer-domain-people-sdk: (reserved for SDK/external consumers).

## Endpoints
- POST `/api/v1/customers` → Register a customer with minimal identity (taxId, name, birthDate, contact). Enforces taxId uniqueness. Returns 201 Created with Location header and the created profile.
- GET `/api/v1/customers/{customerId}` → Retrieve consolidated customer profile (projection/read model). Returns 404 if not found.

Available mock IDs and TAX IDs:
- ID: `11111111-1111-1111-1111-111111111111`, TAX: `TAX111`
- ID: `22222222-2222-2222-2222-222222222222`, TAX: `TAX222`

### Examples
- Register a customer
  ```bash
  curl -i -X POST http://localhost:8080/api/v1/customers \
    -H "Content-Type: application/json" \
    -d '{
      "taxId": "TAX999",
      "name": "Jane Doe",
      "birthDate": "1992-07-15",
      "email": "jane.doe@example.com"
    }'
  ```
  - On duplicate taxId: HTTP 409 Conflict

- Get by id
  ```bash
  curl http://localhost:8080/api/v1/customers/11111111-1111-1111-1111-111111111111
  ```

## OpenAPI / Swagger UI
With SpringDoc for WebFlux: access `/swagger-ui.html` when the app is running.

## Local run
From the project root:
```bash
mvn -q -DskipTests package
mvn -pl customer-domain-people-web spring-boot:run
```
Or run the `CustomerDomainPeopleApplication` class.

## Notes
- Only CQ (queries) is implemented with mock data for reads; registration is mocked via in-memory client.
- The structure is ready to be extended with additional commands and/or full CQRS if required.