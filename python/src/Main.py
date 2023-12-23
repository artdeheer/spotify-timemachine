import TerminalCommandRunner;

# Replace 'folder_path' with the path of the specific folder you want to get files from
files = TerminalCommandRunner.getAllFiles('../../lib')
command_to_run = 'javac -cp "'
count = 0
for file in files:
    command_to_run += file + ':'
    count += 1

command_to_run += '../../java/src" ../../java/src/HistoryAnalyzer.java'

if count == 0:
    command_to_run = 'javac ../../java/src/HistoryAnalyzer.java'

print(command_to_run)
#
# command_to_run = javaCommandLine('javac -cp "')
# Display all file paths
# for file_path in file_paths:
#     print(file_path)

output, error = TerminalCommandRunner.runTerminalCommand('ls')

if error:
    print(f"{error}")
else:
    print(f"Output:\n{output}")

# Run the command
output, error = TerminalCommandRunner.runTerminalCommand(command_to_run)

if error:
    print(f"{error}")
else:
    print(f"Output:\n{output}")

count = 0
newcommand = ''
for letter in command_to_run:
    if count != 4:
        newcommand += letter
    count += 1

command_to_run = newcommand

print(command_to_run)
TerminalCommandRunner.runTerminalCommand(command_to_run)

