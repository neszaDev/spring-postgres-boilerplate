# Contributing

Thanks for your interest in contributing!

## Getting started

1. Fork the repository and create a new branch for your change.
2. Follow the instructions in `docs/development/setup.md` to run the project locally.
3. Run tests: `./scripts/test.sh` (PowerShell: `scripts\test.ps1`).

## Coding guidelines

- Java code should follow common conventions; keep methods small and focused.
- Add tests for bug fixes and new features.
- Keep migration scripts immutable and versioned under `src/main/resources/db/migration`.

## Commit messages

Use concise commit messages. For example:

```
feat(auth): add refresh token rotation
fix(test): stabilize TestResultService test
docs: document deployment steps
```

## Pull request process

- Open a PR against `main` with a clear description using the PR template.
- Ensure CI passes and you've updated documentation when appropriate.
- Assign reviewers and respond to review feedback.

