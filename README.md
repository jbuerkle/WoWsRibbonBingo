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
4. You may invite any number of players into your division.
5. If you play in a division, you and the players in your division may use every ship only **once** per challenge. Your
   ship pool will be shared between players, rather than each player having their own ship pool.
6. Once you start the challenge, every match counts. If you need an exception, you have to
   announce this **before starting the match**. However, you may be granted a chance to retry a level if:
    - The match was less than 12 minutes long, **and** you were alive at the end of the match, or:
    - The player difference at the end of the match was 5 or more players, **and:**
        - In case of a loss, you were one of the last 7 players alive on your team.
        - In case of a win, you were alive at the end of the match.
7. After each match played, your point result is calculated and compared to the result bar of the current level:
    - If your result meets the point requirement, you unlock the reward for the current level.
    - If your result does not meet the point requirement, you lose any unlocked rewards, and the challenge ends.
8. The challenge is "double or nothing" style. After every successful match, you can choose to either:
    - Go to the next level and attempt to double your current reward, or:
    - End the challenge and immediately receive your current reward.

## Point values per ribbon

- Destroyed: 120 points
- Main gun hit: 1 point (3 points for ships with gun caliber of 305mm+ as main armament)
- Secondary hit: 1 point
- Bomb hit: 3 points
- Rocket hit: 3 points
- Citadel hit: 30 points
- Torpedo hit: 30 points (15 points for ships with aircraft as main armament)
- Depth charge hit: 10 points
- Sonar ping: 1 point
- Spotted: 5 points
- Incapacitation: 10 points
- Set on fire: 20 points
- Caused flooding: 40 points
- Aircraft shot down: 10 points
- Shot down by fighter: 10 points
- Captured: 60 points
- Assisted in capture: 30 points
- Defended: 10 points
- Buff picked up: 40 points

## Point values per achievement

- Arsonist: 20 points + 10% bonus points for all 'Set on fire' ribbons
- AA Defense Expert: 5 points + 20% bonus points for all 'Aircraft shot down' ribbons + 20% bonus points for all 'Shot
  down by fighter' ribbons
- Close Quarters Expert: 25 points
- Devastating Strike: 50 points
- Double Strike: 75 points
- Die-Hard: 50 points
- First Blood: 50 points
- It's Just a Flesh Wound: 50 points
- Fireproof: 50 points
- Unsinkable: 50 points
- Dreadnought: 50 points
- Combat Scout: 90 points + 40% bonus points for all 'Spotted' ribbons
- Confederate: 100 points
- High Caliber: 100 points
- Kraken Unleashed: 100 points
- Solo Warrior: 300 points
- Witherer: 40 points + 20% bonus points for all 'Set on fire' ribbons + 10% bonus points for all 'Caused flooding'
  ribbons

Note: Bonus points for the same ribbon stack additively, **not** multiplicatively.

## Result bars and their respective rewards

| Level | Points required | Number of subs as reward: 2^(Level-1) |
|-------|----------------:|--------------------------------------:|
| 1     |             200 |                           2^0 = 1 sub |
| 2     |             400 |                          2^1 = 2 subs |
| 3     |             600 |                          2^2 = 4 subs |
| 4     |             800 |                          2^3 = 8 subs |
| 5     |             950 |                         2^4 = 16 subs |
| 6     |            1100 |                         2^5 = 32 subs |
| 7     |            1250 |                         2^6 = 64 subs |
| 8     |            1400 |                        2^7 = 128 subs |
