FONT_ESC := $(shell printf '\033')
FONT_BOLD := ${FONT_ESC}[1m
FONT_NC := ${FONT_ESC}[0m # No colour

PUBLISH_REPOSITORY := release
VERSION := $(shell git describe --tags --match 'v*' --abbrev=0 | cut -c2-)

ifeq (true,$(prerelease))
	PUBLISH_REPOSITORY := snapshot
else
ifneq (,$(findstring dev,$(VERSION)))
	PUBLISH_REPOSITORY := snapshot
endif
endif

all:
	@echo "Use a specific goal. To list all goals, type 'make help'"

.PHONY: version # Prints project version
version:
	@echo $(VERSION)

.PHONY: dependencies # Lists project dependencies
dependencies:
	@./gradlew clean dependencies

.PHONY: unit-test # Runs unit tests
unit-test:
	@./gradlew clean unitTest

.PHONY: unit-test-publish-report # Runs unit tests and publishes report
unit-test-publish-report:
	@./gradlew clean unitTest coveralls

.PHONY: integration-test # Runs integration tests
integration-test:
	@./gradlew clean integrationTest

.PHONY: build # Builds artifacts
build:
	@./gradlew clean build

.PHONY: static-analysis # Analyzes the build
static-analysis:
	@./gradlew clean check

.PHONY: security-analysis # Runs security analysis looking for vulnerabilities in code
security-analysis:
	@./gradlew clean dependencyCheckAnalyze

.PHONY: publish-local # Publishes artifacts locally
publish-local:
	@./gradlew clean publishToMavenLocal -Pversion=$(VERSION)

.PHONY: publish # Publishes artifacts to the configured remote repository
publish:
	@./gradlew clean publish -Pversion=$(VERSION) -PpublishRepository=$(PUBLISH_REPOSITORY)

.PHONY: help # Generate list of goals with descriptions
help:
	@echo "Available goals:\n"
	@grep '^.PHONY: .* #' Makefile | sed "s/\.PHONY: \(.*\) # \(.*\)/${FONT_BOLD}\1:${FONT_NC}\2~~/" | sed $$'s/~~/\\\n/g' | sed $$'s/~/\\\n\\\t/g'
