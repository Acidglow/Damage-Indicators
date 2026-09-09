# Damage Indicators

Acidglow's Damage Indicators is a NeoForge mod that displays floating damage numbers when entities take damage.

Install it on both the server and every client that should receive and render indicators.

## Features

- Shows floating damage numbers above damaged living entities.
- Categorizes damage as low, high (more than 15 health damage), critical, or entity damage.
- Treats selected damage-over-time sources, such as fire, lava, drowning, freezing, cacti, and wither, as entity/environment damage; other environmental damage is categorized by amount.
- Lets players toggle indicators, adjust text size, choose a custom font, require line of sight, and customize category colors.
- Includes an in-game config screen, opened by default with `P`.

## Requirements

- Minecraft 26.2
- NeoForge 26.2.0.8-beta or newer for Minecraft 26.2
- Java 25

## Building

```sh
./gradlew build
```

The built mod jar is generated in `build/libs/`.

## License

The project's own code and project-owned material are licensed under the MIT
License. The bundled Zebulon Bold Italic font is third-party content licensed
under the SIL Open Font License, Version 1.1; see
[`THIRD_PARTY_NOTICES.md`](THIRD_PARTY_NOTICES.md).
