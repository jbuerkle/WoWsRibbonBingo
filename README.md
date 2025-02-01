# World of Warships Ribbon Bingo

A tool for calculating points in a Twitch streamer challenge for World of Warships

[![Java CI with Maven](https://github.com/jbuerkle/WoWsRibbonBingo/actions/workflows/maven.yml/badge.svg?branch=main&event=push)](https://github.com/jbuerkle/WoWsRibbonBingo/actions/workflows/maven.yml)

In case you want to run the tool for yourself, please check
the [release page](https://github.com/jbuerkle/WoWsRibbonBingo/releases), where you will find a downloadable `.zip`
file. It contains a fully-tested build, packaged as a `.jar` file with all necessary dependencies. Java 23 is required
to run the application, which can be downloaded for free
from [Oracle's official website.](https://www.oracle.com/java/technologies/downloads/)

## Rules for the challenge

1. You have to start the challenge on level 1.
2. You cannot skip any levels, but you can choose to end the challenge early.
3. You have to play the challenge in "Random" matches, with any T6 - T10 ship of your choice.
4. You may invite **one** player into your division, but no more than that.
5. Once you start the challenge, every match counts. There are no second chances. If you need an exception, you have to
   announce this **before starting the match**.
6. After each match played, your point result is calculated and compared to the result bar of the current level:
    - If your result meets the point requirement, you unlock the reward for the current level.
    - If your result does not meet the point requirement, you lose any unlocked rewards, and the challenge ends.
7. The challenge is "double or nothing" style. After every successful match, you can choose to either:
    - Go to the next level and attempt to double your current reward, or:
    - End the challenge and immediately receive your current reward.

## Point values per ribbon

- Destroyed: 120 points
- Main gun hit: 1 point (3x modifier for BB guns)
- Secondary hit: 1 point
- Bomb hit: 2 points
- Rocket hit: 2 points
- Citadel hit: 30 points
- Torpedo hit: 20 points
- Depth charge hit: 10 points
- Sonar ping: 1 point
- Spotted: 5 points
- Incapacitation: 10 points
- Set on fire: 20 points
- Caused flooding: 40 points
- Aircraft shot down: 5 points
- Shot down by fighter: 5 points
- Captured: 60 points
- Assisted in capture: 30 points
- Defended: 10 points
- Buff picked up: 40 points

## Result bars and their respective rewards

| Level | Points required | Number of subs as reward: 2^(Level-1) |
|-------|----------------:|--------------------------------------:|
| 1     |             200 |                           2^0 = 1 sub |
| 2     |             400 |                          2^1 = 2 subs |
| 3     |             550 |                          2^2 = 4 subs |
| 4     |             700 |                          2^3 = 8 subs |
| 5     |             850 |                         2^4 = 16 subs |
| 6     |            1000 |                         2^5 = 32 subs |
| 7     |            1100 |                         2^6 = 64 subs |
| 8     |            1200 |                        2^7 = 128 subs |
