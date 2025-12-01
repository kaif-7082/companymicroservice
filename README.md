**Company Microservice**

-Overview The Company Microservice serves as the central registry for all organizations within the Job Portal ecosystem. It acts as a core data provider and demonstrates Event-Driven Architecture by acting as a Kafka Consumer. It listens for review events to automatically update company ratings in real-time without direct coupling to the review creation process.

**Tech Stack**

* Java 21

* Spring Boot 3.5.7

* Spring Cloud OpenFeign

* Apache Kafka (Consumer)

* MySQL Database

* Micrometer & Zipkin (Distributed Tracing)

* Spring Security (JWT)

**Configuration** This service supports multiple profiles for different environments:

**Development (Dev)**

* Profile: dev

* Port: 8091

* Database: H2 In-Memory

**Production (Prod)**

* Profile: prod

* Port: 8081

* Database: MySQL (companyms_db)

* Application Name: companyms

**Key Features**
* Event-Driven Rating Updates (Kafka Consumer): Listens to the companyRatingQueue topic. When a new review is added in the Review Service, this service consumes the event and updates the company's average rating automatically.

* Role-Based Access Control (RBAC): Secured with JWT tokens. USER Role: Can view company details, download logos, and search companies. ADMIN Role: Has full access to create, update, and delete companies, as well as upload logos.

* Binary File Handling (Logo Management): Supports uploading and storing company logos directly in the database using @Lob (Large Object) storage. Exposes endpoints to download these images via media type headers.

* Advanced Search and Filtering: Provides rich data retrieval options including Pagination, Sorting by specific fields, Filtering by foundation year, and Keyword Search across name, description, and CEO fields.

* Inter-Service Communication: Uses OpenFeign to communicate synchronously with the Review Service to fetch precise average rating calculations when processing Kafka events.

* Data Integrity Validation: Prevents duplicate entries by validating company names before creation or updates. Ensures that critical operations (like deletions) handle non-existent entities gracefully.

**API Endpoints**

| Method | Endpoint | Description | Auth |

| GET | /companies | Get all companies | User/Admin |

| POST | /companies | Create a new company | Admin |

| PUT | /companies/{id} | Update company details | Admin |

| POST | /companies/{id}/logo | Upload company logo | Admin |

| GET | /companies/search | Search companies by text | User/Admin |

| GET | /companies/filterByYear/{year} | Filter by year | User/Admin |


**How to Run**

Infrastructure: Ensure MySQL, Kafka, Zookeeper, Zipkin, and Eureka are running.

Run (default/dev):

./mvnw spring-boot:run

Run (prod):

./mvnw spring-boot:run -Dspring.profiles.active=prod