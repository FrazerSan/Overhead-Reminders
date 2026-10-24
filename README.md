# Overhead Reminders

RuneLite reminders for low prayer and expiring boosts, stacked above your character so simultaneous warnings remain visible.

<img width="457" height="616" alt="image" src="https://github.com/user-attachments/assets/d5cadb28-0d21-4fec-8bfa-e463cc999a39" />


## Features

- Low-prayer warning with a configurable threshold, text, colour, and display duration.
- Independent, vertically stacked messages with optional remaining display seconds.
- Reminders for Overload, Smelling Salts, Divine potions, Saturated Heart, Prayer Regeneration, Prayer Enhance, Thralls, Death Charge, Mark of Darkness, Ward of Arceuus, Stamina, Venom resistance, Antipoison, Antifire, Soulflame Horn, and Surge potions.
- Separate enable, text, and colour settings for each reminder type.
- Duplicate events do not add extra rows or restart an existing message timer.
- Successful thrall, Mark of Darkness, Ward of Arceuus, and Death Charge recasts clear their own reminder. Restoring prayer above the threshold clears Low Prayer and rearms it. Other active messages remain visible.

## Settings

**Overhead Display Time** controls how long messages remain visible (default 3 seconds). Low Prayer has an optional duration override; 0 uses the shared duration. Soulflame Horn uses six seconds and clears when Attack, Strength, or Defence changes.

<img width="221" height="122" alt="image" src="https://github.com/user-attachments/assets/6bca1e5e-aca7-4311-882f-85b4f58ce44f" />


Low Prayer defaults to enabled at threshold 20. It warns once when prayer reaches or falls below the threshold, including login with low prayer. Prayer must rise above the threshold before another warning can occur. Other reminder types default to disabled.

<img width="225" height="188" alt="image" src="https://github.com/user-attachments/assets/06d4b352-c467-4779-85b7-1c67626fe4b3" />

**Show remaining seconds** displays the time until a message disappears; it does not predict prayer drain or buff expiry. **Height above player** adjusts placement. Messages stack upward, oldest at the bottom. Expiring messages disappear independently.

A blank message suppresses that reminder. `{0}` inserts the reminder name. Messages use plain text and their configured colour. Settings are separate from the original plugins and are not imported automatically.

## Installation

**Overhead Reminders** is available in the RuneLite Plugin Hub.

1. Open your normal RuneLite client.
2. Open **Configuration** (the wrench icon), then click **Plugin Hub** at the bottom.
3. Search for **Overhead Reminders** by **FrazerSan**.
4. Click **Install**, then configure the reminders you want to use.

If it does not appear immediately, restart RuneLite and search again. The development client is no longer required to use the plugin.

Disable **Overhead Notifications** and **Prayer Overhead Text** when using this combined plugin to avoid separate duplicate warnings.

## Development and testing

Requires a Java 17 JDK with JAVA_HOME configured. Plugin source targets Java 11. From the project directory:

```powershell
.\gradlew.bat build
.\gradlew.bat run
```

On Linux or macOS, use `./gradlew build` and `./gradlew run`. Run `gradlew.bat test` (or `./gradlew test`) for the automated tests.

The development client registers the plugin directly. For Jagex Accounts, follow [RuneLite's development-client login guide](https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts).

There are 24 automated tests covering thresholds, rearming, stacking, independent expiry, duplicate suppression, successful/failed recasts with live-client message formatting, lifecycle cleanup, Death Charge timing, and rendering. The user has also confirmed gameplay behavior, including low-prayer restoration and thrall/Ward recast clearing.

On Windows, the development run task disables Java 2D interface acceleration to avoid the [known sidebar artifacting issue](https://github.com/runelite/runelite/wiki/Disable-Hardware-Acceleration). This leaves the game's GPU/117 HD plugins available.


## Credits and licensing

Based on [Shuddie's Overhead Notifications](https://github.com/Shuddie/overhead-notifications), with low-prayer behavior adapted from [Xta-Dev's Prayer Overhead Text](https://github.com/Xta-Dev/prayer-notif).

Original BSD-2-Clause licenses are retained in [LICENSE](LICENSE) and [LICENSE-prayer-notif](LICENSE-prayer-notif), and included in the plugin resources. The Death Charge timer also retains its embedded InfernoStats license notice. The combined version adds stacked rendering, keyed expiry and duplicate suppression, a prayer threshold latch, configurable prayer duration, and clearing after successful recasts.
