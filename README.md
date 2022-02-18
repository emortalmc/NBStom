# ExampleExtension
[![license](https://img.shields.io/github/license/Project-Cepi/ExampleExtension?style=for-the-badge&color=b2204c)](../LICENSE)
[![wiki](https://img.shields.io/badge/documentation-wiki-74aad6?style=for-the-badge)](https://project-cepi.github.io/)
[![discord-banner](https://img.shields.io/discord/706185253441634317?label=discord&style=for-the-badge&color=7289da)](https://discord.cepi.world/8K8WMGV)

An example extension for Minestom based off of Sabre/kotlin libs.

## Installation

Download the jar from [Releases](https://github.com/Project-Cepi/ExampleExtension/releases)
OR compile it yourself. Instructions to do so are in Compile header

Drop it into the `/extensions` folder.

## Compile

Create a folder, then
Clone the repository using:

`git clone https://github.com/Project-Cepi/ExampleExtension.git`

Once it is cloned, make sure you have gradle installed, and run

`./gradlew build` on Mac or Linux, and

`gradlew build` on Windows.

This will output the jar to `build/libs` in the project directory.

**Make sure to select the -all jar**. If no shading is necessary, remove the `shadowJar`
