# Changelog

## [Unreleased]
### Added
- Add optional per-world healthbar config

### Changed
- Switch java target from Java 11 to 1.8
- Update gradle to 6.5

## [0.2.0] - 2020-05-17
### Added
- `useMainScoreboard` config option on `SCOREBOARD` healthbars to configure if it should use the main scoreboard (`true`) or a new one (`false`) (default: `false`)
- `duration` config option on `NAME` and `SCOREBOARD` healthbars to configure number of seconds the healthbar shows for

### Changed
- Updated README for clarity around types and styles valid for a specific configuration

### Fixed
- Add explicit exception when healthbar `type` is invalid for a specific bar configuration

## [0.1.3] - 2020-02-11
### Changed
- Updated README.md to include actual information
- Update to v3 of anton-yurchenko/git-release action
- Previous `-all` (fat-jar) distribution is now the default jar and added a `-nokt` distribution without the kotlin stdlib.
  - Users can provide the stdlib on their classpath and avoid every plugin needing to include it.

## [0.1.2] - 2020-01-24
### Changed
- Switched from SimpleMC/git-release back to anton-yurchenko/git-release for release action

### Fixed
- Fixed Damage listener using incorrect value to calculate health (#11)

## [0.1.1] - 2019-12-18
### Added
- Release workflow to add a draft GH release on tag

## [0.1.0] - 2019-12-18
### Added
- Initial release

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

[Unreleased]: https://github.com/SimpleMC/SimpleHealthbars2/compare/release-0.2.0...HEAD
[0.2.0]: https://github.com/SimpleMC/SimpleHealthbars2/compare/release-0.1.3...release-0.2.0
[0.1.3]: https://github.com/SimpleMC/SimpleHealthbars2/compare/release-0.1.2...release-0.1.3
[0.1.2]: https://github.com/SimpleMC/SimpleHealthbars2/compare/release-0.1.1...release-0.1.2
[0.1.1]: https://github.com/SimpleMC/SimpleHealthbars2/compare/release-0.1.0...release-0.1.1
[0.1.0]: https://github.com/SimpleMC/SimpleHealthbars2/releases/tag/release-0.1.0
