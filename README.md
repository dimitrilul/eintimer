# EinTimer

A simple yet powerful timer plugin for Minecraft servers with visual effects.

## Description
> [!WARNING]
> Please use this plugin __only__ on private servers. There are __no__ permissions provided. (everyone can use /timer)

EinTimer provides a customizable timer that displays in the action bar for all online players. The timer features a smooth color transition between pink and purple, creating an appealing visual effect. Available in both German and English editions.

## Features

- Real-time timer displayed in the action bar
- Commands to start, pause, resume, and reset the timer
- Option to automatically pause the timer when a player dies
- Visually appealing color transitions (in a BastiGHG-style)
- Tab completion for commands
- Available in both German and English editions

## Installation

1. Download the EinTimer plugin jar file
2. Place it in your server's `plugins` folder
3. Restart your server
4. The plugin will automatically initialize and start running

## Commands

All commands use the base `/timer` with the following subcommands:

- `/timer enable` - Starts or restarts the timer
- `/timer pause` - Pauses the timer
- `/timer resume` - Resumes a paused timer
- `/timer reset` - Resets the timer to zero and pauses it
- `/timer death` - Toggles automatic pause on player death

## Language Editions

This plugin is available in two language editions:

### German Edition
- User interface messages in German
- Command feedback in German
- Pause message: "Der Timer ist pausiert..."

### English Edition
- User interface messages in English
- Command feedback in English
- Pause message: "The Timer is paused..."

Choose the edition that best suits your server's primary language. The functionality remains identical in both editions.

## Technical Details

- Uses the Bukkit API
- Features efficient color caching for performance
- Implements TabCompleter for command suggestions
- Updates the timer display every second (20 ticks)
- Updates color transitions smoothly using sine wave interpolation

## License

Free to use for any server.
