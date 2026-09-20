## Project Priority

For this Spring Boot project, prioritize application functionality over repository
housekeeping.

Priority order:

1. Application correctness
2. Database/Flyway correctness
3. API behavior
4. Authentication/authorization/security
5. Unit/integration tests
6. Build verification
7. CI
8. Documentation
9. Optional developer tooling

Do not create additional documentation, scripts, GitHub templates, workflows,
or configuration unless the task specifically requires them or they solve an
identified problem.

Do not modify working architecture simply to make it "more production-ready."

Before adding a new dependency or infrastructure component, verify that the
existing project actually needs it.