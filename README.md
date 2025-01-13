# PuzzleHunt

## Group Project for Lab Course: Social Gaming

### Project Title

**PuzzleHunt**

### Team Members

- Yi Rui Cui
- Lukas Pichler
- Thomas Torggler
- Maximilian Amougou Mbida
- Laura Marsoner

### Project Overview

PuzzleHunt is a mobile social gaming application designed to strengthen friendships and enhance social interactions through location-based puzzle collection and trading mechanics.

### Core Features

- **Collect Puzzle Pieces**: Gather puzzle pieces by interacting with the environment.
- **Trading System**: Exchange puzzle pieces with nearby players or friends.
- **Friendship System**: Long-term friendships provide special advantages, including distance trading and gift-sending.
- **Shops and Dealers**: Purchase pieces from shops or gamble for pieces with dealers.
- **Leaderboard**: Track and compare progress with other players.

### Development Timeline

- **Assignment 1**: Sketches of the project concept.
- **Assignment 2**: Development of a Minimum Viable Product (MVP).

### Contextual Design Elements

#### Mobile Context

- **Locations**:
  - Points of Interest (POI) include dealers and shops.
  - Trading mechanics vary based on player density; dealers may disappear with high player traffic.
- **Weather Influence**:
  - Dynamic spawn rates for puzzle pieces based on real-time weather conditions.

#### Short-term Social Context

- **Trading**:
  - Trade through the PuzzleMap or directly with friends.
  - Offer puzzle pieces and confirm trades.

#### Long-term Social Context

- **Friendship Benefits**:
  - Send gifts.
  - Distance trading is enabled.
  - Maintained friendships yield mutual advantages.

### Live Demo Features

- **Login and Profile Management** (Yi Rui Cui)
- **PuzzleMap for Locating Nearby Players and Initiating Trades** (Thomas Torggler)
- **Shop and Dealer for Purchasing and Gambling Puzzle Pieces** (Maximilian Amougou Mbida)
- **Friendlist Management**: Adding friends, viewing profiles, and sending gifts (Laura Marsoner)
- **Inventory Management**: Managing puzzle sets and individual pieces (Lukas Pichler)
- **Leaderboard**: Tracking player rankings

### Technology Stack

- **Android**: Base platform for broad device compatibility.
- **Play! Framework**: Backend RESTful services hosted on Heroku.
- **Firebase**: User authentication and integration with social media.
- **MongoDB**: NoSQL database for scalable data storage.

### APIs and Libraries

- **OpenWeatherAPI**: Weather data for dynamic game elements.
- **Gson**: JSON parsing.

### Individual Contributions

| Team Member            | Responsibilities                                                |
| ---------------------- | --------------------------------------------------------------- |
| **Yi Rui Cui**         | Server development and maintenance, PuzzleMap foundational work |
| **Lukas Pichler**      | Puzzle piece mechanics (splitting, coloring, spawning), Dealer  |
| **Thomas Torggler**    | Inventory system (sets and pieces), Trading functionality       |
| **Maximilian Amougou** | PuzzleShop, Friend management (add/remove, gifting)             |
| **Laura Marsoner**     | UI design (icons, themes), Leaderboard, Weather API integration |

### Sources and References

- REST API Documentation
- MongoDB Logo
- Various online tutorials and documentation for Play! Framework, Firebase, and Android development
