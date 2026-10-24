# Plugin Hub submission

Proposed submission title: **Add Overhead Reminders**

## Pull request description

Overhead Reminders combines expiring-boost reminders with a low-prayer threshold warning. Unlike the base plugin's single overhead-text slot, it draws independently expiring messages in a vertical stack above the local player, keeping simultaneous alerts visible without replacing player chat text.

Each reminder retains configurable enable/text/colour options. Low prayer inherits the shared display duration or uses its own override, alerts once until prayer is restored, and clears above the threshold. Successful thrall, Mark of Darkness, Ward of Arceuus, and Death Charge recasts clear their matching message. Duplicate events do not add rows or extend expiry.

This is based on Shuddie/overhead-notifications and Xta-Dev/prayer-notif. Their BSD-2-Clause licenses and the original timer's embedded notice are retained. The new implementation integrates stacking, independent expiry, threshold rearming, and action clearing as one plugin. No additional runtime dependencies, networking, or game-input automation are introduced.

Validation: 24 automated tests pass, including regressions using actual highlighted game messages. Gameplay testing confirmed the stacked reminders, low-prayer restoration clearing, and thrall/Ward recast clearing. Source targets Java 11; the plugin declares `build=standard`.

Generated-by: OpenAI Codex

## Submission steps

The display name is **Overhead Reminders** and the author is **FrazerSan**. Original authors remain credited in the README and license notices. The repository remote is already set to `https://github.com/FrazerSan/Overhead-Reminders.git`.

1. Commit the project files and push to the public repository. Include source, tests, wrapper, metadata, docs, and licenses. Exclude `build/`, `.gradle/`, and `.tools/` (already ignored).
2. After committing, run `git rev-parse HEAD` to obtain the full 40-character source commit. That commit must be pushed to GitHub.
3. Fork [runelite/plugin-hub](https://github.com/runelite/plugin-hub) and create a branch named `overhead-reminders` in your fork.
4. Add a file named `plugins/overhead-reminders` with exactly these two fields, replacing the commit placeholder with the hash from step 2:

```properties
repository=https://github.com/FrazerSan/Overhead-Reminders.git
commit=PASTE_FULL_40_CHARACTER_COMMIT_HASH_HERE
```

5. Open a pull request from that branch to `runelite/plugin-hub` using the title and description above. Only the `plugins/overhead-reminders` manifest belongs in that pull request; the plugin source stays in your own repository.
6. Check the Hub's build and review feedback. If source changes are needed, push a new source commit and update the manifest in the same pull request.
7. The plugin becomes available after RuneLite accepts and publishes it. Update the README's installation status after acceptance.

The source commit hash can only be filled after you make the commit. The standalone `-all.jar` is a development client launcher and is not uploaded to Plugin Hub.

Official instructions: https://github.com/runelite/plugin-hub#submitting-a-plugin
