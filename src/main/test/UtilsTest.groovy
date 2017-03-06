import net.praqma.Utils


def props = new Properties()
new File("build.properties").withInputStream {
    stream -> props.load(stream)
}

// What's in the properties file?
assert props.buildCmd == 'default.bat'
assert props["buildCmd.win32"] == 'build.bat'
assert props["buildCmd.osx"] == './build-osx.sh'
assert props["buildCmd.linux"] == './build-linux.sh'

// Test the build command resolution...
assert Utils.buildCmd("win32", props) == "build.bat"
assert Utils.buildCmd("osx", props) == "./build-osx.sh"
assert Utils.buildCmd("unknown-platform", props) == "default.bat"
