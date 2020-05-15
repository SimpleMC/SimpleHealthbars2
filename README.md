# SimpleHealthbars2
Simple, easy-to-use healthbar plugin with optional player and mob healthbars

## Features

- Customizable healthbar on the name, scoreboard (below name), or action bar
- Separate healthbar configurations for players and mobs

## Config Overview

```yaml
player-bar:
  type: SCOREBOARD # healthbar type (AKA location, can be SCOREBOARD or ACTION)
  style: ABSOLUTE # style of healthbar (ABSOLUTE, PERCENT, or BAR)
  useMainScoreboard: false # use the main scoreboard (true) or a new scoreboard (false)
  duration: 5 # duration (in seconds) of the healthbar (for NAME or SCOREBOARD type)

mob-bar:
  type: NAME # healthbar type (AKA location, can be SCOREBOARD, NAME, or ACTION)
  style: BAR # style of healthbar (ABSOLUTE, PERCENT, or BAR)
  length: 20 # length of the bar (number of characters)
  char: 0x25ae # character to use for the bar
  showMobNames: true # if the mob's name should show alongside the healthbar (for NAME or ACTION type)
  duration: 5 # duration (in seconds) of the healthbar (for NAME or SCOREBOARD type)
```

### Available `type`s

- `SCOREBOARD` - Below-name healthbar using the scoreboard API (valid for: `player-bar`)
- `NAME` - Updates entity's custom name to include health (valid for: `mob-bar`)
- `ACTION` - Uses a player's action bar to display recently-damaged entity's health (valid for: `player-bar`, `mob-bar`)

### Available `style`s

- `ABSOLUTE` - Health is displayed as actual health value (hearts)
- `PERCENT` - Health is displayed as a percentage of max health
- `BAR` - Health is displayed as a portion of a bar, configured by `length` and `char` properties (not valid for `SCOREBOARD` type bar)
