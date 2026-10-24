# Development

## Build and run

Use a Java 17 JDK. Source and bytecode target Java 11 for RuneLite compatibility.

```powershell
.\dev.cmd build
.\dev.cmd test
.\dev.cmd run
```

The Windows helper detects a portable JDK in `../.tools/jdk17` if present, otherwise uses `JAVA_HOME`. Gradle downloads and caches its dependencies under `../.tools/gradle`. With a configured JDK, `gradlew.bat build`, `gradlew.bat test`, and `gradlew.bat run` work directly. On Linux or macOS use the corresponding `./gradlew` commands.

`run` launches a separate development RuneLite client with Overhead Reminders registered through `ExternalPluginManager.loadBuiltin`. Enable **Overhead Reminders** in the plugin list. Disable the original Overhead Notifications and Prayer Overhead Text plugins to avoid independent duplicate warnings.

For Jagex Accounts, use the client's Jagex auth sidebar and follow the [official instructions](https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts). Developer mode is enabled by the run task.

The build produces:

- `build/libs/overhead-reminders-1.0-SNAPSHOT.jar`: plugin classes and resources.
- `build/libs/overhead-reminders-1.0-SNAPSHOT-all.jar`: development launcher including dependencies.

Standalone Windows development launch:

```powershell
java -ea -Dsun.java2d.d3d=false -Dsun.java2d.opengl=false -jar build/libs/overhead-reminders-1.0-SNAPSHOT-all.jar --developer-mode --debug
```

The Java 2D flags match RuneLite's interface hardware-acceleration OFF setting on Windows. They address [sidebar duplication/ghosting](https://github.com/runelite/runelite/wiki/Disable-Hardware-Acceleration) without disabling the game's GPU or 117 HD plugin. The Gradle run task applies them automatically on Windows. The workaround has been confirmed in gameplay on the development PC.

Do not upload the standalone launcher to Plugin Hub. Hub submission points to the public repository and source commit; RuneLite builds the plugin itself. Build outputs, caches, and portable tools are excluded from Git.

## Verification

The project has 24 automated tests for threshold boundaries, initial-low login, rearming, zero threshold, duration inheritance/override, stacking order and colours, independent expiry, duplicate suppression, blank messages, countdown formatting, targeted clearing, disabled reminders, public-chat rejection, lifecycle cleanup, and Death Charge deadlines/recasts.

Regression tests include the live client's `@mes_hl_*@` highlight codes, including greater impish mage thrall and Ward of Arceuus confirmations. Both these and `<col=...>` formatting are removed before message matching. Failed casts do not clear warnings. A rendering test draws distinct coloured rows at a mocked player position without modifying player overhead text.

Tests and builds have been run with Java 17, Gradle 8.10, and RuneLite 1.13.4. The local build follows RuneLite's `latest.release` convention, so later builds may resolve a newer release. Test reports are under `build/reports/tests/test/index.html`.

The main source has also been compiled against the official Plugin Hub standard-build template with RuneLite 1.13.4 supplied as the client version. This checks source/build compatibility; the Hub's full review and checks run after submission.

Manual gameplay testing has confirmed stacked reminders, low-prayer clearing after restoration, thrall/Ward clearing after successful recasts, and the Windows sidebar workaround.

## Manual regression checklist

1. Let prayer reach the threshold. Confirm one warning that expires without repeatedly restarting. Restore above the threshold while visible: it should clear on the next game tick. Cross again: it should warn again. Restoring only to the threshold should not clear it.
2. Use a longer display duration. Let a thrall and Ward expire, then recast each: only the matching row should disappear. Failed casts should leave rows visible.
3. Set low-prayer duration to 8 seconds and shared duration to 3. Trigger low prayer and a thrall warning together: each should expire independently.
4. Change text, colours, remaining-seconds display, and height. Check spacing while moving, zooming, and rotating the camera.
5. Enable Death Charge and cast it. Confirm the readiness warning after 60 seconds, clearing on a successful recast, and a fresh 60-second deadline.
6. Toggle the plugin, hop worlds, and log out/in. Confirm stale messages clear and a new low-prayer session can warn. Normal region loading should preserve active messages.

## Attribution

Trigger rules and original configuration options come from [Shuddie/overhead-notifications](https://github.com/Shuddie/overhead-notifications). The low-prayer concept and boosted-level check come from [Xta-Dev/prayer-notif](https://github.com/Xta-Dev/prayer-notif). Licenses are retained at the repository root and under `src/main/resources/META-INF/overhead-reminder`; the timer's embedded InfernoStats notice is retained in source and resources.
