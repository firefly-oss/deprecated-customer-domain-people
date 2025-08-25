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
- Formato de fecha: usar "YYYY-MM-DD" (LocalDate) para dateOfBirth e incorporationDate.
- LocalDateTime: los campos validFrom/validTo de statusHistory usan formato ISO-8601 "YYYY-MM-DDTHH:mm:ss" (sin zona horaria) por ser LocalDateTime.
- Enums: party.partyKind y statusHistory[].statusCode deben coincidir con los valores del SDK (se convierten con MapStruct). Si no estás seguro, omítelos para que vayan como null.
- Nulos/omisión: cualquier módulo/campo puede omitirse si no aplica.
- Tipos numéricos: shareCapital es numérico (BigDecimal), headcount es entero.

Ejemplo completo:
```json
{
  "party": {
    "partyKind": "NATURAL_PERSON",
    "preferredLanguage": "es",
    "recordSource": "WEB"
  },
  "naturalPerson": {
    "naturalPersonId": null,
    "partyId": null,
    "title": "Sr.",
    "givenName": "Juan",
    "middleName": "Carlos",
    "familyName1": "Pérez",
    "familyName2": "García",
    "dateOfBirth": "1990-05-15",
    "birthPlace": "Madrid",
    "birthCountryId": 34,
    "nationalityCountryId": 34,
    "gender": "MALE",
    "maritalStatus": "SINGLE",
    "taxIdNumber": "12345678Z",
    "residencyStatus": "RESIDENT",
    "occupation": "Ingeniero",
    "monthlyIncome": 3500.00,
    "suffix": null,
    "createdAt": "2025-08-25T10:09:00",
    "updatedAt": "2025-08-25T10:09:00"
  },
  "legalPerson": {
    "legalEntityId": null,
    "partyId": null,
    "legalName": "Acme S.A.",
    "tradeName": "Acme",
    "registrationNumber": "RM-123456",
    "taxIdNumber": "B12345678",
    "legalFormId": 10,
    "incorporationDate": "2010-03-12",
    "industryDescription": "Fabricación de dispositivos",
    "headcount": 250,
    "shareCapital": 1000000.00,
    "websiteUrl": "https://acme.example.com",
    "incorporationCountryId": 34
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
  "consents": [
    {
      "partyId": null,
      "consentTypeId": 101,
      "isGranted": true,
      "dateGranted": "2025-08-21T18:10:00",
      "dateRevoked": null,
      "grantedChannel": "WEB",
      "revokedChannel": null,
      "revocationReason": null,
      "consentVersion": "v1.0",
      "expiryDate": "2026-08-21T00:00:00",
      "ipAddress": "192.0.2.1",
      "userAgent": "Mozilla/5.0",
      "origin": {
        "consentOriginId": 10,
        "name": null,
        "description": null,
        "consentType": null,
        "isActive": null
      },
      "proof": {
        "consentProofId": null,
        "documentId": 5555
      }
    }
  ],
  "pep": {
    "partyId": null,
    "pep": true,
    "category": "PRIMARY",
    "publicPosition": "Ministro de Hacienda",
    "countryOfPositionId": 34,
    "startDate": "2024-01-01T00:00:00",
    "endDate": null,
    "notes": "Caso de prueba PEP"
  },
  "identityDocuments": [
    {
      "partyId": null,
      "identityDocumentCategoryId": 1,
      "identityDocumentTypeId": 1,
      "documentNumber": "12345678Z",
      "issuingCountryId": 34,
      "issueDate": "2020-05-10T00:00:00",
      "expiryDate": "2030-05-10T00:00:00",
      "issuingAuthority": "DGP",
      "validated": true,
      "documentUri": "https://docs.example.com/dni-12345678z"
    },
    {
      "partyId": null,
      "identityDocumentCategoryId": 2,
      "identityDocumentTypeId": 2,
      "documentNumber": "XK1234567",
      "issuingCountryId": 34,
      "issueDate": "2019-03-01T00:00:00",
      "expiryDate": "2029-03-01T00:00:00",
      "issuingAuthority": "MAEC",
      "validated": false,
      "documentUri": "https://docs.example.com/passport-xk1234567"
    }
  ],
  "addresses": [
    {
      "addressId": null,
      "partyId": null,
      "addressKind": "HOME",
      "line1": "Calle Mayor 1",
      "line2": "Piso 3, Puerta B",
      "city": "Madrid",
      "region": "Madrid",
      "postalCode": "28013",
      "countryId": 34,
      "isPrimary": true,
      "latitude": 40.4168,
      "longitude": -3.7038
    }
  ],
  "emails": [
    {
      "partyId": null,
      "email": "juan.perez@example.com",
      "emailKind": "PERSONAL",
      "isPrimary": true,
      "isVerified": false
    },
    {
      "partyId": null,
      "email": "contacto@acme.example.com",
      "emailKind": "WORK",
      "isPrimary": false,
      "isVerified": true
    }
  ],
  "phones": [
    {
      "partyId": null,
      "phoneNumber": "+34 600 123 456",
      "phoneType": "MOBILE",
      "isPrimary": true,
      "isVerified": false,
      "extension": null
    }
  ],
  "economicActivities": [
    {
      "partyId": null,
      "economicActivityId": 101,
      "annualTurnover": 250000.00,
      "annualIncome": 80000.00,
      "incomeSource": "SALARY",
      "employerName": "Tech Corp",
      "position": "Software Engineer",
      "notes": "Ingreso principal",
      "currencyId": 978,
      "startDate": "2022-01-01T00:00:00",
      "endDate": null,
      "isPrimary": true
    }
  ],
  "providers": [
    {
      "partyId": null,
      "providerTypeId": 1,
      "providerName": "External Provider Corp",
      "providerId": "EXT-001",
      "isActive": true,
      "registrationDate": "2025-01-01T00:00:00"
    }
  ],
  "relationships": [
    {
      "partyId": null,
      "relatedPartyId": 22222222,
      "relationshipTypeId": 1,
      "relationshipKind": "SPOUSE",
      "startDate": "2020-06-15T00:00:00",
      "endDate": null,
      "isActive": true
    }
  ],
  "groupMemberships": [
    {
      "partyId": null,
      "groupId": 1001,
      "membershipTypeId": 1,
      "membershipKind": "FAMILY",
      "joinDate": "2020-01-01T00:00:00",
      "leaveDate": null,
      "isActive": true
    }
  ]
}
```

Ejemplo mínimo (valores básicos):
```json
{
  "party": {
    "partyKind": "NATURAL_PERSON",
    "preferredLanguage": "es",
    "recordSource": "WEB"
  },
  "naturalPerson": {
    "givenName": "Ana",
    "familyName1": "López",
    "dateOfBirth": "1985-10-01"
  },
  "legalPerson": {
    "legalName": "Ejemplo S.L.",
    "incorporationDate": "2015-06-01"
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
    "party": {"partyKind": "NATURAL_PERSON", "preferredLanguage": "es", "recordSource": "WEB"},
    "naturalPerson": {"givenName": "Ana", "familyName1": "López", "dateOfBirth": "1985-10-01"},
    "legalPerson": {"legalName": "Ejemplo S.L.", "incorporationDate": "2015-06-01"},
    "statusHistory": [{"statusCode": "ACTIVE", "validFrom": "2025-08-21T18:05:00"}]
  }'
```