# Makefile for XO Game Android Project

.PHONY: help clean build install test uninstall

# Default target
help:
	@echo "XO Game Android Build Commands:"
	@echo "  make build      - Build debug APK"
	@echo "  make clean      - Clean build artifacts"
	@echo "  make install    - Build and install APK on connected device"
	@echo "  make test       - Run unit tests"
	@echo "  make uninstall  - Uninstall app from connected device"

# Build debug APK
build:
	@echo "Building XO Game APK..."
	./gradlew assembleDebug
	@echo "APK built successfully!"
	@echo "Location: app/build/outputs/apk/debug/app-debug.apk"

# Clean build artifacts
clean:
	@echo "Cleaning build artifacts..."
	./gradlew clean
	@echo "Clean completed!"

# Build and install on connected device
install: build
	@echo "Installing APK on connected device..."
	adb install app/build/outputs/apk/debug/app-debug.apk
	@echo "Installation completed!"

# Run unit tests
test:
	@echo "Running unit tests..."
	./gradlew test
	@echo "Tests completed!"

# Uninstall app from device
uninstall:
	@echo "Uninstalling XO Game..."
	adb uninstall com.xogame.app
	@echo "Uninstallation completed!"

# Full development cycle
dev: clean build install test
	@echo "Development cycle completed!"