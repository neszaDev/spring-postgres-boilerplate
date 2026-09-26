# Release checklist

Before tagging a release (e.g. `v0.1.0`) ensure all checks pass:

1. All CI checks (Spotless, unit tests, CodeQL) are green on main.
2. Integration workflow (integration.yml) has been executed and migrations applied successfully.
3. README and docs are up to date and list required env vars.
4. No TODO or debug artifacts remain in the repository.
5. Tag and push the release: `git tag -a v0.1.0 -m "Release v0.1.0" && git push origin v0.1.0`
