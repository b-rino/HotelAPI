# 🏨 HotelAPI – Backend REST API

HotelAPI er en REST API bygget med fokus på arkitektur, sikkerhed, DevOps-automatisering. Projektet er udviklet som backend-only og deployet via CI/CD til en DigitalOcean-droplet med Docker og Caddy.

## 🚀 Teknologier

- **Javalin** – Letvægts Java web framework med indbygget rollebaseret adgangskontrol via `RouteRole`
- **Hibernate** – ORM til effektiv databasehåndtering med PostgreSQL
- **Lombok** – Reduktion af boilerplate via annoteringer som `@Getter`, `@Builder`, `@AllArgsConstructor`
- **JWT (nimbus-jose-jwt)** – Token-baseret autentificering
- **RestAssured + Hamcrest** – Integrationstest med præcise assertions
- **Docker + GitHub Actions + Watchtower** – CI/CD pipeline med automatisk container-opdatering
- **Caddy** – Reverse proxy med automatisk HTTPS og routing


## 🔐 Fokuspunkter i opgaven

- **Rolle-baseret autorisation** via Javalins `RouteRoles`
- **Token-baseret autentificering** via `JWT`
- **Global exception handling** med strukturerede JSON-fejlbeskeder
- **Global logging** af både requests og responses, inkl. maskering af følsomme felter
- **CI/CD pipeline** der automatisk bygger og deployer

## 🧭 Arkitekturoversigt

HotelAPI følger en klassisk lagdelt struktur:

- **Controller** – Modtager og validerer requests
- **Service** – Indeholder forretningslogik og adgangskontrol
- **DAO (Hibernate)** – Håndterer databaseoperationer

JWT-token verificeres i middleware og adgang håndhæves via `RouteRole`.


## 🧪 Test og kvalitet

HotelAPI er testet med **RestAssured** og **Hamcrest** som integrationstests, der validerer både funktionalitet og fejlhåndtering. Testene er skrevet med fokus på klarhed, robusthed og reviewer-venlighed, og dækker både succesfulde kald og negative scenarier.

### ✅ Dækkede områder

- **Autentificering og token-flow**
    - Login og token-generering
    - Token-validering: gyldig, udløbet, forkert signatur, malformeret og manglende token
    - Rollebaseret adgang: `User` og `Admin` adgang til beskyttede endpoints

- **Registrering**
    - Gyldig brugeroprettelse
    - Duplikat-brugernavn

- **Fejlhåndtering**
    - Global exception handler med struktureret JSON-output
    - Autorisationsfejl med korrekte statuskoder (`401`, `403`, `400`)

- **Hotel-endpoints**
    - Oprettelse, opdatering, sletning og hentning af hoteller
    - Hentning af værelser tilknyttet et hotel
    - Fejl ved forespørgsel på ikke-eksisterende hotel

- **Room-endpoints**
    - Oprettelse og sletning af værelser
    - Fejl ved sletning af ikke-eksisterende værelse

## 📚 JSON-struktur på fejlmeddelelser (exceptions)

```json
{
  "error": "Hotel not found",
  "message": "Hotel with ID 42 does not exist",
  "path": "/api/v1/hotels/42",
  "method": "GET"
}
````


## 📦 Deployment Flow

1. Push til `main` trigger GitHub Actions
2. Docker image bygges og pushes til Docker Hub
3. Watchtower på droplet detekterer nyt image og opdaterer container
4. Caddy reverse proxy håndterer HTTPS og routing


## 🌐 Endpoints og adgang

HotelAPI er live og tilgængelig via følgende base-URL:

**hotel.brino.dk/api/v1**

### 🔑 Autentificering

- `POST /auth/login`  
  Login med brugernavn og adgangskode. Returnerer JWT-token.

- `POST /auth/register`  
  Opret ny bruger. Returnerer JWT-token ved succes.

### 🏨 Hotel-endpoints

- `GET /hotel`  
  Henter alle hoteller

- `GET /hotel/{id}`  
  Henter specifikt hotel

- `GET /hotel/{id}/rooms`  
  Henter værelser tilknyttet hotel

- `POST /hotel`  
  Opret nyt hotel

- `PUT /hotel/{id}`  
  Opdater hotel

- `DELETE /hotel/{id}`  
  Slet hotel

### 🛏️ Room-endpoints

- `POST /room`  
  Opret nyt værelse

- `DELETE /room/{id}`  
  Slet værelse

### 🔐 Beskyttede endpoints

- `GET /protected/user_demo`  
  Kræver gyldigt token med rolle `User`

- `GET /protected/admin_demo`  
  Kræver gyldigt token med rolle `Admin`

> Alle beskyttede endpoints kræver `Authorization: Bearer <token>` i headeren. <br>
> Alle ruter kan også ses live via: [hotel.brino.dk/api/v1/routes](https://hotel.brino.dk/api/v1/routes)

## ⚙️ CLI-scripts til hurtig interaktion

I mappen `/scripts/` findes Bash-scripts der gør det nemt at interagere med API’et direkte fra terminalen — især ved endpoints der kræver et JSON-payload eller autentificering.

GET-endpoints som fx `/hotel` og `/hotel/{id}` kan frit tilgås via browseren, da de ikke kræver token eller request-body.

### Tilgængelige scripts

- `./register-user.sh <username> <password>`  
  Opretter en ny bruger og returnerer JWT-token

- `./login-user.sh <username> <password>`  
  Logger ind og returnerer JWT-token

- `./call-user-demo.sh <token>`  
  Kalder det beskyttede `/protected/user_demo` endpoint med et gyldigt token

- `./create-hotel.sh "<name>" "<address>"`  
  Opretter et hotel

- `./create-room.sh <hotelId> <roomNumber> <price>`  
  Opretter et værelse til et givent hotel

> Scripts bruger `curl` og kræver ingen ekstra afhængigheder. Du kan kopiere token fra login-output og bruge det direkte i `call-user-demo.sh`.


## 🔗 Projektlink

- Live API: [hotel.brino.dk](https://hotel.brino.dk/api/v1/routes)
- GitHub-repo: [github.com/b-rino/HotelAPI](https://github.com/b-rino/HotelAPI)
- Portfolio: [brino.dk](https://brino.dk)


## ⚠️ Disclaimer

Dette projekt er et backend-demo og indeholder ingen følsomme data.  
Alle brugere, tokens og credentials er testdata og kun til udviklingsformål.  
Secrets som `SECRET_KEY` og databaseadgang håndteres via miljøvariabler og er ikke inkluderet i koden eller repository.  
Live API på `hotel.brino.dk` er beskyttet og rate-limited, og bør kun bruges til test og demonstration.





