# FantasticMC

Starter Android project for the FantasticMC companion UI.

- Application ID: `com.fanstatic.mc`
- Accent: `#467CD4`
- Build: GitHub Actions (`.github/workflows/build.yml`)

## Important limitations

This is a starter UI prototype, not a functioning Minecraft mod/client. Android overlays and app-usage detection require the user to grant the relevant Android settings permissions. The HUD rows and floating panel are UI demonstrations; they do not read Minecraft game state, inject code, or change Minecraft gameplay.

## Build APK

After uploading these files to the root of a GitHub repository, open **Actions**, select **Build FantasticMC APK**, and run the workflow (or push a commit). Download the `FantasticMC-debug-apk` artifact from the completed run.
