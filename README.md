# StellarNetApp

Paper plugin for StellarNet network utilities.

## Features

- Random timed announcements
- Clickable announcement links for `store`, `discord`, and `website`
- Configurable announcement prefix from `lang_en.yml`
- Chat games in Java
- Optional PlaceholderAPI expansion
- Reload command for config and language files

## Requirements

- Runtime:
  - Java 21
    - Download: [Eclipse Temurin / Adoptium](https://adoptium.net/)
  - Paper `1.21.8`
    - Download: [Paper Downloads](https://papermc.io/downloads/paper)
- Build:
  - Gradle wrapper included in this project
- Optional:
  - PlaceholderAPI for placeholder support
    - Project page: [PlaceholderAPI on SpigotMC](https://www.spigotmc.org/resources/placeholderapi.6245/)
    - Wiki: [PlaceholderAPI Wiki](https://github.com/PlaceholderAPI/PlaceholderAPI/wiki)

## Installation

1. Build the plugin:

```powershell
./gradlew.bat build
```

2. Copy the generated jar from `build/libs/` into your Paper server `plugins/` folder.

3. Start the server once to generate:

```text
plugins/StellarNetApp/config.yml
plugins/StellarNetApp/lang_en.yml
```

4. Edit config and lang files as needed.

5. Reload or restart the server.

Useful links:

- [PaperMC](https://papermc.io/)
- [Paper Docs](https://docs.papermc.io/)
- [Java Downloads](https://adoptium.net/)

## Build

```powershell
./gradlew.bat build
```

Built jar:

```text
build/libs/
```

## Commands

- `/test`
  Tests the random announcement message.
- `/test chatgame`
  Starts a chat game instantly.
- `/reload`
  Reloads config, lang, and restarts plugin schedulers.
- `/reload config`
  Reloads only config and restarts schedulers.
- `/reload lang`
  Reloads only the language file.

## Permissions

- `stellarnetapp.admin.test`
- `stellarnetapp.admin.reload`

## Config

Main config:

- `language`
- `minMinutes`
- `maxMinutes`
- `announcements.links.discord`
- `announcements.links.store`
- `announcements.links.website`
- `announcements.sound.name`
- `announcements.sound.volume`
- `announcements.sound.pitch`
- `chatgames.enabled`
- `chatgames.interval_minutes`
- `chatgames.time_limit_seconds`
- `chatgames.max_number`
- `chatgames.reward_text`
- `chatgames.scramble_words`

Language file:

- `announcements_messages.prefix`
- `announcements_messages.hover`
- `announcements_messages.variants.*.messages`
- `chatgames.scramble.messages`
- `chatgames.math.messages`
- `chatgames.winner.messages`
- `chatgames.timeout.messages`

## Announcement Links

Use link tokens inside `lang_en.yml`:

```yml
{LINK:store}
{LINK:discord}
{LINK:website}
```

These names must match keys under:

```yml
announcements:
  links:
    discord: "https://example.com"
    store: "https://example.com"
    website: "https://example.com"
```

## Chat Games

Chat games rotate automatically based on config and currently include:

- Scramble word game
- Math question game

Answers are matched exactly from player chat.

## Notes

- `run/plugins/StellarNetApp/` contains the active runtime config/lang copies used by the local test server.
- If you change only source resource files, rebuild and replace the plugin jar.
- If you want immediate local testing, also update the runtime files or use the reload command.
