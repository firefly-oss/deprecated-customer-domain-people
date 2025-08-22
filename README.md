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

## Ejemplos JSON (RegisterCustomerCommand: party + naturalPerson + legalPerson + statusHistory)
A continuación se muestran bloques JSON de ejemplo para invocar el endpoint POST /api/v1/customers con el record RegisterCustomerCommand, incluyendo los módulos RegisterPartyCommand (party), RegisterNaturalPersonCommand (naturalPerson), RegisterLegalPersonCommand (legalPerson) y RegisterPartyStatusEntryCommand (statusHistory).

Notas:
- Formato de fecha: usar "YYYY-MM-DD" (LocalDate) para dateOfBirth y dateOfIncorporation.
- LocalDateTime: los campos validFrom/validTo de statusHistory usan formato ISO-8601 "YYYY-MM-DDTHH:mm:ss" (sin zona horaria) por ser LocalDateTime.
- Enums: party.partyType y statusHistory[].statusCode deben coincidir con los valores del SDK (se convierten con MapStruct). Si no estás seguro, omítelos para que vayan como null.
- Nulos/omisión: cualquier módulo/campo puede omitirse si no aplica.
- Tipos numéricos: shareCapital es numérico (BigDecimal), numberOfEmployees es entero.

Ejemplo completo:
```json
{
  "party": {
    "partyType": "NATURAL_PERSON",
    "preferredLanguage": "es",
    "recordSource": "WEB"
  },
  "naturalPerson": {
    "partyId": null,
    "firstName": "Juan",
    "middleName": "Carlos",
    "firstSurname": "Pérez",
    "secondSurname": "García",
    "dateOfBirth": "1990-05-15",
    "placeOfBirth": "Madrid",
    "gender": "MALE",
    "maritalStatus": "SINGLE",
    "taxIdentificationNumber": "12345678Z",
    "residencyStatus": "RESIDENT",
    "titleId": 1,
    "nationalityId": 34,
    "countryOfResidenceId": 34,
    "avatarUrl": "https://example.com/avatar.jpg"
  },
  "legalPerson": {
    "partyId": null,
    "legalName": "Acme S.A.",
    "tradeName": "Acme",
    "registrationNumber": "RM-123456",
    "taxIdentificationNumber": "B12345678",
    "legalFormId": 10,
    "dateOfIncorporation": "2010-03-12",
    "businessActivity": "Fabricación de dispositivos",
    "numberOfEmployees": 250,
    "shareCapital": 1000000.00,
    "websiteUrl": "https://acme.example.com",
    "incorporationCountry": "ES",
    "phoneNumber": "+34 900 123 456",
    "emailAddress": "contacto@acme.example.com",
    "mainContactName": "María Rodríguez",
    "mainContactTitle": "Directora General",
    "logoUrl": "https://acme.example.com/logo.png"
  },
  "statusHistory": [
    {
      "partyId": null,
      "statusCode": "ACTIVE",
      "statusReason": "Alta inicial",
      "validFrom": "2025-08-21T18:05:00",
      "validTo": null
    }
  ],
  "pep": {
    "partyId": null,
    "isPep": true,
    "pepCategory": "PRIMARY",
    "publicPosition": "Ministro de Hacienda",
    "countryOfPosition": "ES",
    "startDate": "2024-01-01T00:00:00",
    "endDate": null,
    "notes": "Caso de prueba PEP"
  },
  "identityDocuments": [
    {
      "partyId": null,
      "identityDocumentTypeId": 1,
      "documentNumber": "12345678Z",
      "countryOfIssue": "ES",
      "issueDate": "2020-05-10T00:00:00",
      "expiryDate": "2030-05-10T00:00:00",
      "issuingAuthority": "DGP",
      "isValidated": true,
      "documentId": 1001
    },
    {
      "partyId": null,
      "identityDocumentTypeId": 2,
      "documentNumber": "XK1234567",
      "countryOfIssue": "ES",
      "issueDate": "2019-03-01T00:00:00",
      "expiryDate": "2029-03-01T00:00:00",
      "issuingAuthority": "MAEC",
      "isValidated": false,
      "documentId": 1002
    }
  ],
  "addresses": [
    {
      "partyId": null,
      "addressType": "HOME",
      "addressLine1": "Calle Mayor 1",
      "addressLine2": "Piso 3, Puerta B",
      "city": "Madrid",
      "province": "Madrid",
      "postalCode": "28013",
      "country": "España",
      "isPrimary": true,
      "latitude": 40.4168,
      "longitude": -3.7038,
      "normalizationId": "norm-123",
      "isVerified": true,
      "formattedAddress": "Calle Mayor 1, 28013 Madrid, España",
      "administrativeAreaLevel1": "Comunidad de Madrid",
      "administrativeAreaLevel2": "Madrid",
      "countryCode": "ES",
      "administrativeAreaLevel1Id": 13,
      "administrativeAreaLevel2Id": 28079,
      "countryId": 34
    }
  ],
  "emails": [
    {
      "partyId": null,
      "emailAddress": "juan.perez@example.com",
      "emailType": "PERSONAL",
      "isPrimary": true,
      "isVerified": false
    },
    {
      "partyId": null,
      "emailAddress": "contacto@acme.example.com",
      "emailType": "WORK",
      "isPrimary": false,
      "isVerified": true
    }
  ]
}
```

Ejemplo mínimo (valores básicos):
```json
{
  "party": {
    "partyType": "NATURAL_PERSON",
    "preferredLanguage": "es",
    "recordSource": "WEB"
  },
  "naturalPerson": {
    "firstName": "Ana",
    "firstSurname": "López",
    "dateOfBirth": "1985-10-01"
  },
  "legalPerson": {
    "legalName": "Ejemplo S.L.",
    "dateOfIncorporation": "2015-06-01"
  },
  "statusHistory": [
    {
      "statusCode": "ACTIVE",
      "validFrom": "2025-08-21T18:05:00"
    }
  ]
}
```

Ejemplo de llamada (PowerShell):
```powershell
curl -Method Post `
  -Uri "http://localhost:8080/api/v1/customers" `
  -ContentType "application/json" `
  -Body '{
    "party": {"partyType": "NATURAL_PERSON", "preferredLanguage": "es", "recordSource": "WEB"},
    "naturalPerson": {"firstName": "Ana", "firstSurname": "López", "dateOfBirth": "1985-10-01"},
    "legalPerson": {"legalName": "Ejemplo S.L.", "dateOfIncorporation": "2015-06-01"},
    "statusHistory": [{"statusCode": "ACTIVE", "validFrom": "2025-08-21T18:05:00"}]
  }'
```
