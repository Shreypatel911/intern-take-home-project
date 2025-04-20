# Software requirements to run this project

- Java (JDK 11 or higher)
- Apache Maven

# Steps to download softwares on Windows

- Java
  - Download and install JDK 11 or higher
  - Add path to the bin folder to your System Path (e.g. C:\Program Files\Java\jdk-11\bin)
  - Verify using command 'java -version' in your terminal, It should show the version of Java installed
- Maven
  - Install Apache Maven
  - Extract the zip file
  - Add path to the bin folder to your System Path (e.g. C:\Program Files\Apache\apache-maven-3.9.6\bin)
  - Verify using command 'mvn -v' in your terminal, It should show the version of Maven installed

# Steps to download softwares on Mac

- Java
  - Download and install JDK 11 or higher
  - Install the .pkg file
  - Edit shell config to add the path to the bin folder
    - e.g. `export PATH="/Library/Java/JavaVirtualMachines/temurin-11.jdk/Contents/Home/bin:$PATH"`
  - Apply changes
    - source ~/.zshrc or ~/.bash_profile
  - Verify using command 'java -version' in your terminal, It should show the version of Java installed
 
- Maven
  - Download and extract .tar.gz to a folder
  - Add Maven's bin folder to PATH
    - e.g. `export PATH="/Users/yourname/tools/apache-maven-3.9.6/bin:$PATH"`
  - Apply changes
    - source ~/.zshrc
  - Verify using command 'mvn -v' in your terminal, It should show the version of Maven installed
 
# Steps to run the program (Same for both Windows and Mac)

- Clone the project
- Open the project in Intellij or just terminal is also fine
- Run command 'mvn clean compile'
- Run command 'mvn exec:java'

# How to give input after the project is running successfully (Same for both Windows and Mac)

- It will show you two options for input - From JSON file or From API endpoint, choose one of those two, whichever you want to test with
- If you choose input from JSON file then type name of the file and this file should be in the resources folder (src/main/resources),
  press enter and you will see the output
- If you choose input from API endpoint then it will ask for four things 1) [ENDPOINT] 2) [YEAR] 3) [MONTH] 4) [DAY], which are from
  `https://[ENDPOINT]/_cat/indices/*[YEAR]*[MONTH]*[DAY]?v&h=index,pri.store.size,pri&format=json&bytes=b`, after providing this one by one
  press enter and you will see the output
