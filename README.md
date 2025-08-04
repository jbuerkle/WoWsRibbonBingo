# World of Warships Ribbon Bingo

A tool for calculating points in a Twitch streamer challenge for World of Warships

[![Java CI with Maven](https://github.com/jbuerkle/WoWsRibbonBingo/actions/workflows/maven.yml/badge.svg?branch=main&event=push)](https://github.com/jbuerkle/WoWsRibbonBingo/actions/workflows/maven.yml)

In case you want to run the tool for yourself, please check
the [release page](https://github.com/jbuerkle/WoWsRibbonBingo/releases), where you will find a downloadable `.zip`
file. It contains a fully-tested build, packaged as a `.jar` file with all necessary dependencies. Java 23 is required
to run the application, which can be downloaded for free
from [Oracle's official website.](https://www.oracle.com/java/technologies/downloads/)

## Rules for the solo streamer challenge (classic)

1. You have to start the challenge on level 1.
2. You cannot skip any levels, but you can choose to end the challenge early. You cannot do this while in a match.
3. You have to play the challenge in "Random" matches, with any T6 - T10 ship of your choice. **Exception:** Hybrid
   ships which have player-controllable, carrier-like planes are banned from use for the main participant.
4. You may invite any number of players into your division. However, they will join you as supporters, and any ribbons
   or achievements collected by them will **not** award any points.
5. If you play in a division, you and the players in your division may use every ship only **once** per challenge. Your
   ship pool will be shared between players, rather than each player having their own ship pool. However, only ships
   used in successful matches as per rule 7 are counted.
6. Once you start the challenge, every match counts. If you need an exception or want to pause the challenge, you have
   to announce this **before starting the match**.
7. After every match played, your point result is calculated and compared to the result bar of the current level:
    - If your result meets the point requirement, you unlock the reward for the current level. You automatically go to
      the next level (unless you unlocked the final reward).
    - If your result does not meet the point requirement, you lose any unlocked rewards, and the challenge ends.
8. Exceptions to rule 7 which allow you to retry and keep your unlocked rewards:
    - 8a: The match was less than 14 minutes long, **and** you were alive at the end of the match.
    - 8b: The player difference at the end of the match was 5 or more players, **and:**
        - In case of a win, you were alive at the end of the match.
        - In case of a loss (or a draw), you were one of the last 7 players alive on your team.
    - 8c: You had an unfair disadvantage (examples: obvious stream sniping, AFK player or griefer on your team). This is
      ultimately at the discretion of the challenge's host.
    - 8d: You have an extra life.
9. Rules for extra lives:
    - 9a: You gain 1 token for every successful match as per rule 7.
    - 9b: You gain 1 token for every match which applies to rule 8a or 8b. This rule stacks with rule 9a.
    - 9c: 6 tokens are automatically converted to 1 extra life.
    - 9d: Whenever an unsuccessful match would otherwise end the challenge for you, in case you have an extra life, it
      is automatically consumed, and you keep any unlocked rewards (rule 8d).
    - 9e: You do not gain any tokens for completing the final level of the challenge.
    - 9f: Unused extra lives are converted to 6 subs each at the end of the challenge. Unused tokens are not converted.
10. The challenge is "double or nothing" style. Every successful match doubles your current reward, but as long as you
    do not have any extra lives, you are at risk of losing your unlocked rewards to an unsuccessful match. While not in
    a match, you can always choose to receive your current reward instead of continuing the challenge.

## Rules for the duo/trio streamer challenge

1. All rules of the solo streamer challenge also apply to this variant of the challenge, unless specified otherwise.
2. All participants **must** stream for the entire duration of the challenge.
3. Extends classic rule 3: Hybrid ships are banned from use for **all** participants.
4. Replaces classic rule 4:
    - All players in your division join as participants in the challenge.
    - Players **cannot** join your division as supporters.
    - Participating streamers cannot be switched out for other streamers.
    - All ribbons and achievements unlocked by any of the players in your division award points.
    - Each player's result will be calculated separately, then combined into one result.
5. Extends classic rule 8: Any of the retry rules apply to the division as a whole **if:**
    - In a duo, the classic retry rule applies to at least **one** of the two players.
    - In a trio, the classic retry rule applies to at least **two** of the three players.
6. Some additional rules apply to the result bars and rewards. Please check
   [the section below](#result-bars-and-their-respective-rewards) for details.

## Point values per ribbon

- Destroyed: 120 points
- Main gun hit: 1 point (2 points for ships with gun caliber of 203mm up to 304mm as main armament, 3 points for ships
  with gun caliber of 305mm up to 405mm as main armament, 4 points for ships with gun caliber of 406mm or above as main
  armament)
- Secondary hit: 1 point
- Bomb hit: 3 points
- Rocket hit: 3 points
- Citadel hit: 20 points (40 points for ships with gun caliber of 203mm up to 304mm as main armament, 60 points for
  ships with gun caliber of 305mm up to 405mm as main armament, 80 points for ships with gun caliber of 406mm or above
  as main armament)
- Torpedo hit: 40 points (20 points for ships with aircraft as main armament)
- Depth charge hit: 10 points
- Sonar ping: 5 points
- Spotted: 30 points (10 points for ships with aircraft as main armament)
- Incapacitation: 10 points
- Set on fire: 20 points
- Caused flooding: 40 points
- Aircraft shot down: 10 points
- Shot down by fighter: 10 points
- Captured: 80 points
- Assisted in capture: 40 points
- Defended: 10 points
- Buff picked up: 60 points

## Point values per achievement

- Arsonist: 30 points + 10% bonus points for all 'Set on fire' ribbons
- AA Defense Expert: 45 points + 30% bonus points for all 'Aircraft shot down' ribbons + 30% bonus points for all 'Shot
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
- Combat Scout: 60 points + 60% bonus points for all 'Spotted' ribbons
- Confederate: 150 points
- High Caliber: 150 points
- Kraken Unleashed: 30 points + 20% bonus points for all 'Destroyed' ribbons
- Solo Warrior: 300 points
- Witherer: 30 points + 30% bonus points for all 'Set on fire' ribbons + 30% bonus points for all 'Caused flooding'
  ribbons

Note: Bonus points for the same ribbon stack additively, **not** multiplicatively.

## Point values per division achievement

- General Offensive: 100 points (+50% bonus points for duos)
- Brothers-in-Arms: 150 points
- Strike Team: 250 points (+50% bonus points for duos)
- Coordinated Attack: 200 points (+50% bonus points for duos)
- Shoulder to Shoulder: 250 points (+50% bonus points for duos)

Note:

- As division achievements are awarded to all members of a division at the same time, the points are shared between
  players (so they only award points once).
- This section applies to the duo/trio streamer challenge only. In the solo streamer challenge, division achievements
  will **not** award any points.

## Result bars and their respective rewards

| Level | Points required | Number of subs as reward: 2^(Level) |
|-------|----------------:|------------------------------------:|
| 0     |               0 |                      2^0 = 1 sub 🎁 |
| 1     |             300 |                     2^1 = 2 subs 🎁 |
| 2     |             500 |                     2^2 = 4 subs 🎁 |
| 3     |             700 |                     2^3 = 8 subs 🎁 |
| 4     |             900 |                    2^4 = 16 subs 🎁 |
| 5     |            1200 |                    2^5 = 32 subs 🎁 |
| 6     |            1500 |                    2^6 = 64 subs 🎁 |
| 7     |            1800 |                   2^7 = 128 subs 🎁 |

Note:

- The level 0 reward is unlocked just by participating in the challenge. You will receive it in case you do not unlock
  any higher reward.
- The point requirements listed here apply to the solo streamer challenge. In the duo/trio streamer challenge, the point
  requirements for each level increase by 40% for every additional player (duo +40%, trio +80%).
- In the duo/trio streamer challenge, the reward **must** be shared between the participating streamers. Every streamer
  must receive at least a 25% share of the total reward. The only exception are small rewards (1 sub cannot be split).
- As the reward is paid in gift subs, all participating streamers **must** have a monetized stream on Twitch.

## Optional ship restrictions (not part of the main rule set)

For streamers who participated frequently in the challenge, additional ship restrictions may apply. At the start of
every level, all possible ship restrictions are put into a random order. The streamer picks any positive integer
(including 0), which corresponds to one of these restrictions:

- 50% chance for ships with a certain type of main armament to be banned from use in the current level
- 50% chance to be forced to use a ship with a certain type of main armament in the current level

The types of main armaments which can occur are:

- Gun calibers of 202mm or below
- Gun calibers of 203mm up to 304mm
- Gun calibers of 305mm up to 405mm
- Gun calibers of 406mm or above

Each of these categories has an equal chance to occur (25%). A perfectly equal distribution is **always** guaranteed on
every level, which means the chance for any particular combination to be picked is 12.5%. The main deciding factor is
the number picked by the streamer.

## Hall of Fame

### Solo streamer challenge (classic)

| Twitch streamer | Date played (dd.mm.yyyy) | Supporters                | Total reward |
|-----------------|--------------------------|---------------------------|-------------:|
| OverLordBou     | 21.02.2025               | Your_SAT_Score            |  140 subs 🎁 |
| OverLordBou     | 04.04.2025               | Your_SAT_Score            |  140 subs 🎁 |
| Hyf1re          | 10.04.2025               | ArmoredGuppy, OverLordBou |  140 subs 🎁 |
| OverLordBou     | 18.04.2025               | Ausomaster                |  140 subs 🎁 |
| OverLordBou     | 02.05.2025               | Your_SAT_Score, StarboyNA |  134 subs 🎁 |
| Hyf1re          | 08.05.2025               | ArmoredGuppy              |  128 subs 🎁 |
| OverLordBou     | 16.05.2025               | Your_SAT_Score, StarboyNA |  134 subs 🎁 |
| OverLordBou     | 26.06.2025               | Your_SAT_Score, StarboyNA |  134 subs 🎁 |
| Hyf1re          | 03.07.2025               | ArmoredGuppy              |  128 subs 🎁 |
| Daniel_Russev   | 04.08.2025               | Senpai_Silent             |  134 subs 🎁 |

### Duo/trio streamer challenge

| Twitch streamers | Date played (dd.mm.yyyy) | Total reward |
|------------------|--------------------------|-------------:|
