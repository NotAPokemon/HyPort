# HyPort

HyPort is a small Java tool that automates building and installing a target GitHub repository. It downloads a repo, runs configured pre-install/build steps, and copies the resulting JAR into the current working directory using a configurable naming protocol.

This repository contains the HyPort tool itself and an example configuration file `hyport-config.json` used for testing.

--

## Quick Start

Prerequisites

- Java 21 (project uses a Java 21 toolchain).
- Git (to allow `RepoExtractor` to fetch repositories) and network access
- Windows/MacOs/Linux

See the **Configuration** section below for how `hyport-config.json` controls behavior.

## Configuration (`hyport-config.json`)

The `hyport-config.json` file at the repository root provides behavior for installing your plugin. Important keys:

- `ProjectName` (string): the project name used when creating the final installed JAR filename.
- `Version` (string): optional version used with the `name-version` protocol.
- `Pre-Install` (array): commands to run before copying the JAR. Each entry is an object that may contain:
  - `system` (optional): `win` or `unix` — run only on matching OS.
  - `exe` (string): the executable to run (e.g. `./gradlew` or `./gradlew.bat`).
  - `args` (array of strings): arguments to pass to the executable.
- `jar` (string): path (relative to the extracted repository root) to the JAR to copy. If omitted, looks for `build/libs/<repo>.jar`.
- `install-protocal` (string): controls the final filename. Supported values:
  - `name-only` — final filename: `<ProjectName>.jar`
  - `name-version` — final filename: `<ProjectName>-<Version>.jar`
  - `custom|||<name>` — final filename is the literal `<name>` after the `custom|||` prefix

Note: uses the `system` field to decide which pre-install commands to run on Windows vs Unix.

## Troubleshooting

- Ensure that your hyport-config.json is in the root directory of your repo
- Ensure that your pre - install config will out put a jar to the path you specify in the config or `build/libs/<repo>.jar`

## Development notes & next steps

- Currently just a plain java executable but in the future will be designed to work as a plugin for a hytale server
  - command to use when integrated `/install <Owner> <Plugin-repo>` (e.g `/install NotAPokemon HyTale`)
- Currently no diffrentiation between Mac and linux for preinstall commands
