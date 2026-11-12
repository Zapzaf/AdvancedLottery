# AdvancedLottery (26.3 fork)

> **This is a fork of [AdvancedLottery](https://www.spigotmc.org/resources/%E2%9C%A8advancedlottery%EF%B8%8F%EF%B8%8F-1-8-1-17-gui-menu-op-lottery-mysql-placeholder-api.43668/) by ianlim224.**
> All credit for the original plugin goes to its author. This fork updates it to run on
> Minecraft **26.3** and is not affiliated with or supported by the original author.

A GUI lottery plugin for Spigot/Paper servers with Vault economy support, MySQL stats, and PlaceholderAPI placeholders.

## Compatibility

| | Original | This fork |
|---|---|---|
| Minecraft | 1.8 – 1.19 | **26.3** |
| Java | 8 | **25** |
| Required | Vault + an economy plugin | Vault + an economy plugin |
| Optional | PlaceholderAPI, Essentials | PlaceholderAPI, Essentials |

Support for pre-1.13 servers has been dropped.

## What changed in this fork

- Built against `spigot-api 26.3-R0.1-SNAPSHOT` and compiled for Java 25. `plugin.yml` declares `api-version: '26.3'`.
- Removed the XSeries dependency. Config materials are now resolved by a built-in `Materials` helper.
  Legacy names from old configs (for example `WATCH`, `BOOK_AND_QUILL`, `STAINED_GLASS_PANE:5`) are still
  accepted and mapped to their modern equivalents. Unknown materials log a warning and fall back to `STONE`.
- Win titles use Bukkit's `Player#sendTitle`, and sounds use the `Sound` constants (`Sound` is no longer an enum).
- Removed the legacy (pre-1.13) skull implementation.
- Replaced Apache Commons Lang 2, which the server no longer ships, with small helpers in `SpigotCommons`.
  The ticket amount in `/lottery addtickets` is now checked for int overflow.
- Fixed missing commas in time strings when a unit in the middle is zero (e.g. `2 hours5 seconds` → `2 hours, 5 seconds`).
- Default materials updated to modern names (`CLOCK`, `WRITABLE_BOOK`).
- Upgraded HikariCP to 7.1.0 (relocated in the shaded jar), PlaceholderAPI to 2.11.6 and VaultAPI to 1.7.1.
- Added unit tests (JUnit 6 + Mockito).

## Building

Requires JDK 25 and Maven 3.9+.

```sh
mvn package
```

The plugin jar is written to `target/AdvancedLottery-<version>.jar`. Copy it (not the `original-` jar) into your server's `plugins/` folder.

## Running the tests

```sh
mvn test
```

The tests cover material resolution (including every default config material), time parsing and formatting,
cooldowns, the lottery pot, number and placeholder helpers, and update-check version comparison.
They run without a Minecraft server.

## Upgrading from the original plugin

Your existing `config.yml` and data should keep working. Legacy material names are translated automatically.
To get rid of the warnings and fallbacks, replace any material that logs an "Unknown material" warning with its
modern name (see the [Material list](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html)).

## License

This project is released under the GNU General Public License v3.0, the same license as the original.
See [LICENSE](LICENSE).
