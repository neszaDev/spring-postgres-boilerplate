"
.PHONY: build test docker-build docker-up

build:
	./scripts/build.sh

test:
	./scripts/test.sh

docker-build:
	docker compose --profile dev build
	docker compose --profile prod build

docker-up:
	docker compose --profile local up --build
"
