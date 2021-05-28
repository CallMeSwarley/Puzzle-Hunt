Requirements:
	JDK 1.8.0_291
		-	install
		-	set system variable JAVA_HOME to jdk1.8.0_291
		-	remove PATH entry containing "..\Java\javapath"
		-	add %JAVA_HOME%\bin to Path
		-	restart pc
	SBT
		-	install
		-	restart pc
	Android Studio
		-	install
		-	install scala plugin
		-	import android project
		-	import server project	
			-	"import project from external model" -> sbt
			-	use detected jdk
			-	overwrite file
Optional Requirements:
	using own smartphone
		-	get usb drivers for smartphone brand
	deploy to Heroku
		-	see README.MD in PlayServer
