# University Marketplace

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
University Marketplace is one-stop-shop for college students to buy and sell their textbooks and supplies to other college students within their university. Students can also upload any resources such as Textbook PDFs and studyguides that can be accessed by students in need.

### App Evaluation

- **Category:** Lifestyle
- **Mobile:** 
    - Buyers can chat with sellers in real time.
    - Sellers can share their location with the buyer for a meet up to make the purchase.
    - Sellers can use their phone camera to quickly take pictures for their listings
- **Story:**
    - This would be a very valuable resource for students who are on a budget when it comes to textbooks and school supplies.
    - Students can share free PDFs and study guides for their classmates, building a hub that can be more accessible than private Facebook groups
- **Market:**
    - Market is centered around college students
    - The app would filter a student's experience based on the college they attend, making it a perfect for a niche group
- **Habit:**
    - Students would use this app at the beginning and end of the semester to buy and sell any books they might have no need for anymore or need for the upcoming semester.
- **Scope:**
    - The basic features are possible to complete during this time
    - The skeleton cna still be visually appealing and fun to build

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Listings Timeline
    * Filter by college in timeline
    * Endless scroll
    * Detail view on click
        * Chat with seller on messenger
* "Post a listing" user flow (Fill form --> post --> edit --> mark as sold)
* Account View
    * Basic Info
    * My listings
* Register
* Log in
* Log out

**Optional Nice-to-have Stories**

* Resources Tab
    * Dowloadable PDFs + Images
* Internal chat feature
* Multiple filters for listings Timeline
* Verification of Users through student IDs etc
* Support for multiple colleges where filter is done automatically for each user

### 2. Screen Archetypes

* Listings Timeline
    * Filter by college in timeline
    * Endless scroll
    * Detail view on click
* Listing Detail View
   * More info (Description, Pictures, etc)
   * Info of seller
   * Button to chat with seller (Redirect to messenger/internal ca)
* Account View (User Profile)
    * My listings
    * Basic Info
* Register
    * Make internal account or sign in with google
* Log in
* (Optional) Settings
* (Optional) Resources
    * Organized file system with free PDFs and study resources that can be uploaded

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Timeline
* Resources
* My Profile
* Post

**Flow Navigation** (Screen to Screen)

* Log in
    * Register
    * Timeline
* Log in
   * Timeline
   * Detail View
   * Chat with Seller
* Resources (optional)
   * Detail View
       * Click to dowload
* Timeline (Post)
    * Post Listing Form
    * My Profile
* My Profile
    * Detail View of my listing
    * Marked as sold confirmation
    * My Profile
* My Profile
    * Options/Setting (Select sign out)
    * Log in page 
    

## Wireframes
![image](https://user-images.githubusercontent.com/61126997/124189056-22439e00-da8e-11eb-933a-bdc3e94d8328.png)

### Digital Wireframes & Mockups
https://www.figma.com/file/YpCxirYonq54XBNBdL7OXN/University-Marketplace?node-id=0%3A1

### Interactive Prototype
![App Walkthrough](screenshots/Marketplace_App_Design_Walkthrough.gif)

## Schema 
### Models

#### Listing

|   Property  |       Type      |              Description             |
|:-----------:|:---------------:|:------------------------------------:|
| objectId    | String          | Unique id for the listing            |
| user        | Pointer to User | The user who posted the listing      |
| images      | File Array      | Array of images uploaded by the user |
| title       | String          | Title for listing                    |
| description | String          | Description of item being sold       |
| price       | Number          | Price set for the item by the user   |
| course      | String          | Course (if any) the item is for      |

### Resource

|    Property   |       Type      |              Description             |
|:-------------:|:---------------:|:------------------------------------:|
| objectId      | String          | Unique id for the listing            |
| user          | Pointer to User | The user who posted the listing      |
| images        | File Array      | Array of images uploaded by the user |
| title         | String          | Title for listing                    |
| course        | String          | Course (if any) the item is for      |
| resource_type | String          | Textbook, studyguide, other          |
| file          | File            | The uploaded PDF or image file       |
| file_preview  | File            | Image preview of the PDF uploaded    |

### User

|  Property |            Type           |                Description               |
|:---------:|:-------------------------:|:----------------------------------------:|
| objectId  | String                    | Unique id for user                       |
| name      | String                    | Name of user                             |
| school    | String                    | School the user attends                  |
| email     | String                    | User’s email                             |
| username  | String                    | User’s username                          |
| password  | String                    | User’s password                          |
| listings  | Pointer to Listing Array  | List of the listings posted by the user  |
| resources | Pointer to Resource Array | List of the resources posted by the user |

### Message

| Property |       Type      |          Description          |
|:--------:|:---------------:|:-----------------------------:|
| objectId | String          | Unique id for message         |
| user     | Pointer to User | The user who sent the message |
| body     | String          | The message itself (the text) |

### Conversation

| Property |           Type           |                   Description                   |
|:--------:|:------------------------:|:-----------------------------------------------:|
| objectId | String                   | Unique id for conversation                      |
| user1    | Pointer to User          | Pointer to one of the users in the conversation |
| user2    | Pointer to User          | Pointer to the other user in the conversation   |
| messages | Pointer to Message Array | Array of messages between the two users         |

   
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
