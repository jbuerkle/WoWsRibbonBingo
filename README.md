# World of Warships Ribbon Bingo

A tool for calculating points in a Twitch streamer challenge for World of Warships

[![Java CI with Maven](https://github.com/jbuerkle/WoWsRibbonBingo/actions/workflows/maven.yml/badge.svg?branch=main&event=push)](https://github.com/jbuerkle/WoWsRibbonBingo/actions/workflows/maven.yml)

## Frequently Asked Questions (FAQ)

1. **Q: What is this challenge about?**

   A: You can think of this challenge as similar to the in-game "Naval Battles" for ribbons, except here both ribbons
   and achievements count. On top of that, each ribbon and achievement has a point value, so that those which are harder
   to obtain will give you more points. There are also extra rules to make the challenge less reliant on random factors.

2. **Q: What is in it for the streamer?**

   A: There are rewards for both the streamer and the streamer's community, in the form of gift subs. In essence, this
   means there is money to be won.

3. **Q: What is in it for the challenge host?**

   A: Mostly entertainment. The challenge host also gets to support streamers they enjoy watching in a unique way.

4. **Q: How do I join?**

   A: It goes by the Hollywood principle: Do not ask to be invited, the challenge host will invite you.

5. **Q: I have been invited. What do I need to do now?**

   A: Read this FAQ, and the rules below. If you want to participate, let the challenge host know on what day and at
   what time you want to participate, including your local time zone. The challenge host will try to join your stream at
   your preferred time. In case you want to participate in the solo streamer challenge, there is nothing else you have
   to do. In case you want to participate in the duo/trio streamer challenge, you are responsible for inviting the other
   streamers and sharing this webpage with them.

6. **Q: That is so much text. Can I not just ask questions, and you answer all of them one by one?**

   A: Clarifying questions are always welcome. However, please do not expect the challenge host to answer questions if
   it is obvious that you have not read any of the rules on this page. Writing everything down in detail took a lot of
   time. Also, if you are a participating streamer, not knowing the rules properly will put you at a disadvantage.

7. **Q: Are you affiliated with Wargaming? Who is paying for this challenge?**

   A: No, the challenge host is neither affiliated with Wargaming, nor is anyone else providing funds. All money is
   coming from the challenge host's personal pocket.

8. **Q: How often can I participate in this challenge?**

   A: In general, you can participate an unlimited amount of times. However, if you get a decent payout, your next
   challenge attempt is on cooldown for a **minimum** of four weeks. Depending on the amount of money you received, your
   cooldown may be considerably longer, up to **eight weeks**. In case of a failed challenge attempt, there will not be
   any cooldown, and you will usually be able to retry on the following day.

9. **Q: Since I was invited once, can I expect to participate in the challenge at regular intervals?**

   A: No, getting invited once does **not** mean you are permanently entitled to participate. Depending on the challenge
   host's availability of time and funds, as well as personal interest in the game and stream, you may not be able to
   participate again in the future. The challenge host can cancel planned challenge attempts for any reason.

10. **Q: Do I need to download this tool to participate?**

    A: No, that is completely optional. The challenge host will use it to calculate your points and share match results
    with you on stream.

## Steps to install and run the tool

In case you want to run the tool yourself (for example, to get a better understanding of how the challenge works, or to
cross-check results from your side), please follow the steps below:

1. Open the [release page.](https://github.com/jbuerkle/WoWsRibbonBingo/releases)
2. Download the `WoWsRibbonBingo_jar.zip` file from the "Assets" section of the latest release.
3. Open the `.zip` file, then copy the folder inside to a directory you like. However, you should select a directory
   that does not require administrative rights to modify. For example, on Windows: `C:\Users\your_username`
4. Open [Oracle's official webpage for Java downloads.](https://www.oracle.com/java/technologies/downloads/)
5. Click on the tab for "JDK 25" (or higher), then click on the tab for your operating system (for example, "Windows"),
   then click on a download link (for example, "x64 Installer").
6. Double-click on the downloaded installer and follow the steps to install Java.
7. After finishing the installation, go to the directory where you put the `WoWsRibbonBingo_jar` folder and find the
   `WoWsRibbonBingo.jar` file inside. If it does not automatically show up with a coffee icon, then select Java as the
   default application to run `.jar` files. For example, on Windows: Right-click on that file, select "open with",
   select "other app", select "Java(TM) Platform SE binary", then click on button "always".
8. A warning about not being able to create the autosave folder shows up because the application was started through
   the "open with" option. Click on the "OK" button, then close the application.
9. Double-click on the `WoWsRibbonBingo.jar` file to start the application normally. The warning should **not** show up
   this time. If it is still shown, you may have actually put the application into a folder which requires
   administrative rights to modify. In this case, please refer to step 3.

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
      the next level (unless you unlocked the final reward). This counts as a successful match.
    - If your result does not meet the point requirement, you lose any unlocked rewards, and the challenge ends. This
      counts as an unsuccessful match.
8. You keep your unlocked rewards and get to retry the level if 5 or more conditions from the following list apply in a
   match you played:
    - 8a: +1 if the match was less than 14 minutes long (*)
    - 8b: +1 if any team reaches 0 points or 1000 points (*)
    - 8c: +1 if the team difference at the end of the match was 4 or more ships (*)
    - 8d: +1 if the team difference at the end of the match was 6 or more ships (*)
    - 8e: +1 if your ship was **not** one of the first 4 to sink on your team
    - 8f: +1 if your ship was **not** one of the first 6 to sink on your team
    - 8g: +1 if your ship was afloat near the end of the match (last 90 seconds are irrelevant)
    - 8h: +1 if you are among the top 6 base XP earners on your team
    - 8i: +1 if you are among the top 3 base XP earners on your team
    - 8j: +1 if you are the top base XP earner on your team
    - 8k: +1 for every four ships missing from your match (10v10, 8v8, 6v6, etc.)
    - 8l: +1 for every AFK player on your team
    - 8m: +1 for every griefer on your team
    - 8n: +2 for obvious stream sniping by enemy players (ultimately at the discretion of the challenge host)
    - 8o: If your actions directly prevented any condition marked with a star (*) from applying, because you tried to
      keep your team from losing, then the condition will still apply.
9. Rules for extra lives:
    - 9a: You gain 1 token for every successful match as per rule 7.
    - 9b: You gain 1 token for every match in which you are allowed to retry as per rule 8.
    - 9c: 6 tokens are automatically converted to 1 extra life.
    - 9d: Whenever an unsuccessful match would otherwise end the challenge for you, in case you have an extra life, it
      is automatically consumed, you keep your unlocked rewards and get to retry the level.
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
- Assisted in destruction: 60 points
- Main gun hit: 1 point (2 points for ships with 203–304mm guns as main armament, 3 points for ships with 305–405mm guns
  as main armament, 4 points for ships with 406mm+ guns as main armament)
- Secondary hit: 1 point
- Bomb hit: 3 points
- Rocket hit: 3 points
- Citadel hit: 20 points (40 points for ships with 203–304mm guns as main armament, 60 points for ships with 305–405mm
  guns as main armament, 80 points for ships with 406mm+ guns as main armament)
- Torpedo hit: 40 points (20 points for ships with aircraft as main armament)
- Depth charge hit: 10 points
- Sonar ping: 5 points
- Spotted: 30 points (15 points for ships with aircraft as main armament)
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
- Witherer: 60 points + 30% bonus points for all 'Set on fire' ribbons + 30% bonus points for all 'Caused flooding'
  ribbons

Note: Bonus points for the same ribbon stack additively, **not** multiplicatively.

## Point values per division achievement

- General Offensive: 100 points (+50% bonus points for duos)
- Brothers-in-Arms: 150 points
- Strike Team: 150 points (+50% bonus points for duos)
- Coordinated Attack: 150 points (+50% bonus points for duos)
- Shoulder to Shoulder: 150 points (+50% bonus points for duos)

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
  requirements for each level increase by 60% for two players, and by 100% for three players.
- In the duo/trio streamer challenge, the reward **must** be shared between the participating streamers. Every streamer
  must receive at least a 25% share of the total reward. The only exception are small rewards (1 sub cannot be split).
- As the reward is paid in gift subs, all participating streamers **must** have a monetized stream on Twitch.

## Optional challenge modifiers

Before starting the challenge, participating streamers can pick from a list of challenge modifiers, which will make the
challenge harder for them, but also yield a higher potential reward. Once the challenge has started, the choice of
challenge modifiers is permanent, and cannot be changed until the end of the current challenge attempt. You can pick any
combination of the following challenge modifiers (including none, or all of them):

- Random ship restrictions: All participating streamers get random ship restrictions, as described
  in [the section below](#optional-ship-restrictions), in exchange for +50% additional rewards.
- Increased difficulty: The point requirements for each level increase by 20%, in exchange for +25% additional rewards.
- Double difficulty increase: The point requirements for each level increase by another 20%, in exchange for +25%
  additional rewards. Duo/trio streamer challenge only.
- No help: Supporters cannot join your division, in exchange for +25% additional rewards. Solo streamer challenge only.
- No giving up: You cannot end the challenge early, in exchange for +25% additional rewards. This does not affect your
  ability to pause the challenge.
- No safety net: You do not gain any extra lives, in exchange for +75% additional rewards.

Note:

- All bonus rewards from challenge modifiers, as well as all point requirement increases of any type stack additively,
  **not** multiplicatively.
- Any number higher than or equal to 0.5 subs will be rounded up to 1 sub, while any number lower than that will be
  rounded down to 0 subs.

## Optional ship restrictions

At the start of every level, all participating streamers pick a positive integer (including 0), which corresponds to one
of the following restrictions:

- 50% chance that you **cannot** use ships with a certain type of main armament in the current level
- 50% chance that you **must** use ships with a certain type of main armament in the current level

The types of main armaments which can occur are:

- 1–202mm guns
- 203–304mm guns
- 305–405mm guns
- 406mm+ guns

Each of these categories has an equal chance to occur (25%). A perfectly equal distribution is **always** guaranteed on
every level, which means the chance for any particular combination to be picked is 12.5%. The main deciding factor is
the number picked by the streamer. Note that all possible ship restrictions are put into a random order every time a
streamer has picked a number, which means the result of a given number will never be predictable.

The other two categories of main armaments (aircraft for aircraft carriers and torpedoes for submarines) will never
occur here. On the one hand, this means you will never be forced to play aircraft carriers or submarines. On the other
hand, if you **do** want to play aircraft carriers or submarines, then there is still the same 50% chance that you will
be forced to play something else.

## Leaderboard

### Solo streamer challenge (classic)

| Rank | Twitch streamer | Date played (dd.mm.yyyy) | Supporters                | Total reward |
|------|-----------------|--------------------------|---------------------------|-------------:|
| 1.🥇 | OverLordBou     | 18.12.2025               | -                         |  280 subs 🎁 |
| 2.🥈 | W0rldSp0wn      | 14.01.2026               | -                         |  268 subs 🎁 |
| 3.🥉 | Hyf1re          | 22.01.2026               | -                         |  219 subs 🎁 |
| 4.   | W0rldSp0wn      | 16.12.2025               | BenzyMH, MWOMA            |  175 subs 🎁 |
| 5.   | W0rldSp0wn      | 17.11.2025               | -                         |  160 subs 🎁 |
| 6.   | Daniel_Russev   | 21.12.2025               | Seidlhannes               |  160 subs 🎁 |
| 7.   | OverLordBou     | 18.04.2025               | Ausomaster                |  140 subs 🎁 |
| 8.   | OverLordBou     | 02.05.2025               | Your_SAT_Score, StarboyNA |  134 subs 🎁 |
| 9.   | OverLordBou     | 16.05.2025               | Your_SAT_Score, StarboyNA |  134 subs 🎁 |
| 10.  | OverLordBou     | 26.06.2025               | Your_SAT_Score, StarboyNA |  134 subs 🎁 |

### Duo/trio streamer challenge

| Rank | Twitch streamers       | Date played (dd.mm.yyyy) | Total reward |
|------|------------------------|--------------------------|-------------:|
| 1.🥇 | OverLordBou, StarboyNA | 24.09.2025               |  320 subs 🎁 |
| 2.🥈 | OverLordBou, StarboyNA | 06.11.2025               |  320 subs 🎁 |
| 3.🥉 | Hyf1re, ArmoredGuppy   | 28.09.2025               |  235 subs 🎁 |
| 4.   | Hyf1re, ArmoredGuppy   | 17.08.2025               |  158 subs 🎁 |
