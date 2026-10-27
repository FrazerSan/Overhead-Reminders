# Overhead Reminders

RuneLite reminders for low prayer and expiring boosts, stacked above your character so simultaneous warnings remain visible.

<img width="318" height="561" alt="image" src="https://github.com/user-attachments/assets/1d7a2244-0443-47cc-90fc-c8eed2f6d24a" />


## Features

- Low-prayer warning with a configurable threshold, text, colour, and display duration.
- Special attack energy reminders at optional 25%, 50%, 75% and 100% milestones, with full energy selected by default.
- Independent, vertically stacked messages with optional remaining display seconds, configurable position, size, and spacing, alongside a preview quick action.
- Reminders for Overload, Smelling Salts, Liquid Adrenaline, Divine potions, Melee(strength) Ranged and Magic based on threshold, Saturated Heart, Prayer Regeneration, Prayer Enhance, Thralls, Death Charge, Mark of Darkness, Ward of Arceuus, Stamina, Venom resistance, Antipoison, Antifire, Soulflame Horn, and Surge potions.
- Separate enable, text, and colour settings for each reminder type.
- Reapplying a potion or heart effect clears its reminder, including all Antifire variants. Combat and pie reminders clear when their stat is boosted again. Successful spell recasts clear their own reminder. Restoring prayer above the threshold clears Low Prayer and rearms it. Soulflame Horn announces its active effect and clears on a melee stat change.

## Settings

Reminder sections start collapsed. RuneLite remembers opened/closed sections while the client is running; a full restart starts them collapsed again.

<img width="240" height="485" alt="image" src="https://github.com/user-attachments/assets/cd341ac4-5026-45df-9e91-3e951622d217" />


**Overhead Display Time** is the shared display duration (default 15 seconds, maximum 300 seconds). Every reminder has a **Use global timer** toggle, enabled by default. Turn it off to use that reminder's own display time. **0** always means no timeout, whether chosen globally or individually. Existing custom duration choices are retained on upgrade.



Low Prayer uses a threshold when enabled. It warns once when prayer reaches or falls below the threshold, including login with low prayer. Prayer must rise above the threshold before another warning can occur.

<img width="239" height="220" alt="image" src="https://github.com/user-attachments/assets/5a23ecf6-76d2-4f51-badf-fc6fc16f0d1d" />


Use **Quick actions** at the top of the plugin settings for Show preview, Stop preview, Enable all, Disable all, Reset appearance, Reset timers, Reset messages, **Use global timer for all**, or **Use individual timers for all**. The sidebar icon is hidden by default; turn on **Show sidebar icon** if you prefer the same tools as sidebar buttons. Bulk enabling and disabling keeps your text, thresholds, colours and timers. Switching all timer toggles keeps the saved custom durations. Bulk changes and resets reopen the settings view so its checkboxes and values immediately reflect the saved settings. Reset timers restores 15 seconds, global toggles on, early warnings off, and countdown off. Reset messages restores wording while keeping colours.

<img width="237" height="322" alt="image" src="https://github.com/user-attachments/assets/8c767143-6c0a-40ab-b2cc-041877476330" />


**Show six-message preview** displays Low Prayer, Divine, Soulflame Horn, Thrall, Surge and Overload together, using your current text and colours even if those reminders are disabled. Change position, size or spacing in Configuration while the samples stay visible, then click **Stop preview**. Real reminders continue to be tracked separately. Preview also stops on logout or disabling the plugin.


**Show out-of-supplies** Enabled by default, it checks potion, heart and salts supplies automatically. A due reminder with no suitable supply shows **Out of [item name]**, using the last suitable item seen (without its dose count), or a reminder-name fallback. It keeps the same colour and original timeout. Obtaining supplies restores the normal prompt without adding a row or restarting the timer. Turn this switch off to keep normal reminder text regardless of inventory. Low Prayer, spells, pie tracking and the active Soulflame Horn message are unaffected.

<img width="379" height="303" alt="image" src="https://github.com/user-attachments/assets/a797ad78-2068-4a8c-9c39-47e29eb638bf" />



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

There are 123 automated tests covering thresholds, rearming, stacking, independent expiry, duplicate suppression, successful/failed recasts with live-client message formatting, lifecycle cleanup, Death Charge timing, rendering, combat inventory checks across styles, boost thresholds, fixed-effect exclusions, pie tracking, reboost clearing, optional sidebar visibility and quick actions. The user has also confirmed gameplay behavior, including low-prayer restoration and thrall/Ward recast clearing.

On Windows, the development run task disables Java 2D interface acceleration to avoid the [known sidebar artifacting issue](https://github.com/runelite/runelite/wiki/Disable-Hardware-Acceleration). This leaves the game's GPU/117 HD plugins available.




## Credits and licensing

Based on [Shuddie's Overhead Notifications](https://github.com/Shuddie/overhead-notifications), with low-prayer behavior adapted from [Xta-Dev's Prayer Overhead Text](https://github.com/Xta-Dev/prayer-notif).

Original BSD-2-Clause licenses are retained in [LICENSE](LICENSE) and [LICENSE-prayer-notif](LICENSE-prayer-notif), and included in the plugin resources. The Death Charge timer also retains its embedded InfernoStats license notice. The combined version adds stacked rendering, keyed expiry and duplicate suppression, a prayer threshold latch, configurable prayer duration, and clearing after successful recasts.

The optional combat boost reminders are inspired by [AnkouOSRS/unpotted-reminder](https://github.com/AnkouOSRS/unpotted-reminder). Its potion-family and stat-change approach informed this implementation; the original notices are retained in [LICENSE-unpotted-reminder](LICENSE-unpotted-reminder) and the plugin resources.
