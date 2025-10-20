# Software Engineering

> The project name in the title aims to describe the purpose of the project and help generate initial interest by presenting the core goal of the project. It entirely depends on you!
> 
> Of course, no template is ideal for all projects because the needs and goals are different. Don’t hesitate to emphasize your goal on this project’s introductory page; we’ll support it, whether you focus more on technology or marketing.
> 
> Why This Document? 
This document serves as both a template and a resource for tracking the progress and structure of your project. It is a de facto standard for ensuring clear documentation of key aspects of your work. By providing essential information, you make it easier to follow your development process and assess the quality of your project.
> 
> Maintaining a well-organized document reflects good project management practices and promotes transparency, collaboration, and accountability within your team. It simplifies the understanding of your project’s scope, goals, and challenges, benefiting not only your team but also anyone who reviews your work.

## Project Description

This project is the result of teamwork as part of the project assignment for the [Software Engineering](https://www.fer.unizg.hr/predmet/proinz) course at the Faculty of Electrical Engineering and Computing, University of Zagreb.

Briefly describe the goal of your project. What motivated you? What problem are you solving?

> Since this is a part of course assigment, also mention what new things you want/have learned.
> 
> A well-written description allows you to showcase your work to other developers, as well as potential employers. Not only does the first impression on the description page often distinguish a good project from a bad one, but it also represents good practice that you must master.

## Functional Requirements

| ID  | Description                                                                                                                           | Priority Level (1 to 5) | Target role(s) : Users, Unauthenticated users, Admins |
| --- | ------------------------------------------------------------------------------------------------------------------------------------- | ----------------------- | ----------------------------------------------------- |
| F1  | Allows new users to create an account by using OAuth 2.0 providers such as Google, Apple, and Microsoft                               | 5                       | Unauthenticated users                                 |
| F2  | Allows users who already have an account to access it using OAuth 2.0 providers such as Google, Apple and Microsoft                   | 5                       | Unauthenticated users                                 |
| F3  | Allows users to delete their account and all associated data                                                                          | 4                       | Users                                                 |
| F4  | Allows new users who just created their account to fill in a short onboarding questionnaire covering stress level, sleep quality, ... | 3                       | Users                                                 |
| F5  | Generates a personalized seven-day practice plan for new users based on their responses in the onboarding questionnaire (see F4)      | 3                       | Users                                                 |
| F6  | Allows users to update their personal information, preferences and account settings                                                   | 4                       | Users                                                 |
| F7  | Provide a map of the nearest psychologist                                                                                             | 2                       | Users                                                 |
| F8  | Allows users to rate the services (from 1 to 5) and provide a comment after each session                                              | 4                       | Users                                                 |
| F9  | Moderate the inappropriate content                                                                                                    | 3                       | Admins                                                |
| F10 | Allows users to use their own calendar like Google Calendar or other CalDAV-<br>compatible services                                   | 4                       | Users                                                 |
| F11 | Manage roles/permision with RBAC                                                                                                      | 5                       | Users/Admins                                          |
| F12 | Recommendation system based on the user's goals, preferences, history, mood, and time of day.                                         | 3                       | Users                                                 |
| F13 | Notifications for upcoming sessions                                                                                                   | 3                       | Users                                                 |
| F14 | Log useful information about authentification and RBAC and users/admin actions                                                        | 5                       | Users/Admins                                          |
| F15 | Store guided meditations in audio and video formats by fetching them using an API, with duration, language, difficulty level and tags | 4                       | Admins                                                |
| F16 | Organize meditations by categories (focus, sleep, ...)                                                                                | 3                       | Admins                                                |
| F17 | Allows users to do a daily challenge with a meditation and a short exercise                                                           | 4                       | Users                                                 |
| F18 | Allows users to get badges if they do streaks for their daily challenges (see F17)                                                    | 3                       | Users                                                 |
| F19 | Display average ratings and reviews for each session                                                                                  | 3                       | Admins                                                |
| F20 | Allow users to perform daily mood check-ins with a 1–10 scale, emotion tags, and optional notes                                       | 4                       | Users                                                 |
| F21 | Track habits such as sleep quality, stress, focus, caffeine/alcohol intake, and physical activity                                     | 3                       | Users                                                 |
| F22 | Integrate with external sleep tracking services and display sleep score data (0–100)                                                  | 3                       | Users                                                 |
| F23 | Allow trainers/therapists to publish training plans and track user progress with consent                                              | 2                       | Trainers                                              |
| F24 | Enable users to schedule recurring practice sessions and sync them to external calendars                                              | 4                       | Users                                                 |
| F25 | Display user progress and weekly summaries (mood trends, streaks, time spent)                                                         | 3                       | Users                                                 |
| F26 | Ensure compliance with GDPR (user consent, right to deletion/export, data minimization)                                               | 5                       | Users/Admins                                          |
| F27 | Send weekly motivational or progress emails/push notifications                                                                        | 3                       | Users                                                 |

## Deployment
This section provides information on how to access and evaluate the deployed demo version of the application, allowing users to interact with the app and test its functionality in a limited scope before running it locally.

For a demonstration of the application's functionality, a demo version is available at *[link to deployment]*. This version allows users to explore key features and interact with the app in a limited scope.

### Accessing the Test Application
> - **Visit the Demo Link**: Navigate to the provided URL to access the live demo version of the application.
> - **Test Features**: You can explore the core functionalities, such as [list key features].
> - **Limited Scope?**: Please note that this demo version may have limited features or data for testing purposes.

For detailed instructions on installation and running the application in a local environment, please refer to the documentation in the [link to Wiki].

## Installation

> - Provide clear instructions on how to install and run the project or [link to Wiki].
> - Include dependencies (if any) and installation commands.
> - Consider offering pre-built binaries or Docker images (if applicable).

## Technologies

- Backend / API : Spring
- Frontend : Angular
- SQL Database
- Firebase for authentication and notifications

## Team Members

- Matis DUBOIS (team leader)
- Matthieu CHARTON
- Evan LEMONNIER
- Martin NERON
- Lucas MAGRET
- Melissa GHAOUI

## Contributions (Optional)
> Rules depend on the team’s organization and are often outlined in `CONTRIBUTING.md`.




# 📝 Licence
Važeča (1)
[![CC BY-NC-SA 4.0][cc-by-nc-sa-shield]][cc-by-nc-sa]

This repository contains open educational resources and is licensed under the Creative Commons license, which allows you to download, share, and use the work as long as you attribute the author, do not use it for commercial purposes, and share it under the same conditions Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License HR.

**Note**:  
> All packages are distributed under their own licenses.  
> All used materials (images, models, animations, etc.) are distributed under their own licenses.

> **Important Note for Students**: When contributing to this project or using open-source code, ensure that you respect the licensing terms of any third-party libraries or dependencies. All contributions to the project should be licensed under an open-source license, such as the one provided by GitHub or similar platforms. Please refer to the licensing details of the project repository for more information.

Additionally, any used materials (images, models, animations, etc.) must be in compliance with their respective licenses.

> ## AI Usage

> When AI technologies, models, or services were used in this project, it is essential to clearly state and reference them in the documentation. This includes any machine learning models, AI frameworks, or third-party services integrated into the application. Be transparent about the AI components, their functionality, sources, and ensure compliance with relevant usage and licensing terms.
> 
> Additionally, consider the ethical implications of using AI in your project, particularly in terms of user privacy, data security, and fairness. Ensure that the AI models or services respect user confidentiality and do not introduce bias or discrimination.
> 
> For example, if AI services such as natural language processing, image recognition, or recommendation algorithms were utilized, provide appropriate citations or links to the resources used, along with any relevant documentation or guidelines for their usage.
> 
> If you are using third-party AI models, libraries, or services, ensure proper credit is given, and comply with their licensing conditions. Always strive to integrate AI functionalities responsibly, aligning them with the project's overall ethical standards and ensuring that they contribute positively to the user experience and societal values.

[![CC BY-NC-SA 4.0][cc-by-nc-sa-image]][cc-by-nc-sa]

[cc-by-nc-sa]: https://creativecommons.org/licenses/by-nc/4.0/deed.hr 
[cc-by-nc-sa-image]: https://licensebuttons.net/l/by-nc-sa/4.0/88x31.png
[cc-by-nc-sa-shield]: https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0-lightgrey.svg

Orginal [![cc0-1.0][cc0-1.0-shield]][cc0-1.0]
>
>COPYING: All the content within this repository is dedicated to the public domain under the CC0 1.0 Universal (CC0 1.0) Public Domain Dedication.
>
[![CC0-1.0][cc0-1.0-image]][cc0-1.0]

[cc0-1.0]: https://creativecommons.org/licenses/by/1.0/deed.en
[cc0-1.0-image]: https://licensebuttons.net/l/by/1.0/88x31.png
[cc0-1.0-shield]: https://img.shields.io/badge/License-CC0--1.0-lightgrey.svg

### Reference na licenciranje repozitorija

# 📝 Code of Conduct [![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg)](CODE_OF_CONDUCT.md)
As students, you are surely familiar with the minimum acceptable behavior defined in the **STUDENT CODE OF CONDUCT** of the Faculty of Electrical Engineering and Computing, University of Zagreb, as well as additional guidelines for teamwork in the Software Engineering course.

We expect you to follow the **[IEEE Code of Ethics](https://www.ieee.org/about/corporate/governance/p7-8.html)**, which plays an important educational role in setting the highest standards of integrity, responsible behavior, and ethical conduct in professional activities. By doing so, the professional community of software engineers defines general principles that establish moral character, guide important business decisions, and set clear moral expectations for all members of the community.

The Code of Conduct is a set of enforceable rules that serve to clearly communicate expectations and requirements for the community/team's work. It defines obligations, rights, unacceptable behaviors, and appropriate consequences (unlike the ethical code). In this repository, one of the widely accepted codes of conduct for working in open-source communities is provided.

> ### Improve Team Functionality:
> - Define how work will be distributed among team members.
> - Agree on how the team will communicate.
> - Don’t waste time on deciding how the group will resolve disputes—apply the standards!
> - It is implicitly assumed that all team members will follow the code of conduct.
> 
> ## Issue Reporting
> 
> The worst thing that can happen is for someone to remain silent when there are problems. There are several things you can do to best resolve conflicts and issues:
> 
> - Contact me directly via [e-mail](mailto:vlado.sruk@fer.hr), and we will do everything we can to confidentially understand what steps we need to take to resolve the problem.
> - Talk to your assistant, as they have the best insight into the team dynamics. Together, you’ll figure out how to resolve the conflict and how to avoid further impact on your work.
> - If you feel comfortable, discuss the problem directly. Minor incidents should be resolved directly. Take time and privately speak with the affected team member and trust in their sincerity.