# JSTemplate

JSTemplate is a project that uses JavaScript Nashorn to build dynamic templates. It allows to program templates using Javascript, with full access to Java libraries through Nashorn.

## Getting Started

Download <a href="https://github.com/helliu/JSTemplate/blob/master/JSTemplateShowCase.jar">JSTemplateShowCase.jar</a> and play around.
<br/>
```java
<#
    //javascript code here 
    var className = "HelloWorld";
#>

public class <#=className#>{

}
```
Will generate
```java


public class HelloWorld{

}
```

### Installing
Download <a href="https://github.com/helliu/JSTemplate/blob/master/bin/JSTemplate.jar">JSTemplate.jar</a>.
<br />
Call by command line:
```
sintax: 
java -jar JSTemplate.jar <TEMPLATE_FILE_NAME>

example: 
java -jar JSTemplate.jar template.js
```

Or add to your Java Project and call by:
```java
try {
	JSTemplateResult result = JSTemplate.execute("template code here");
	
	//template result
	System.out.println(result.getValue());
} catch (JSTemplateException e) {
	e.printStackTrace();
}
```

### Prerequisites

Java 8


## Examples
### Hello World
```java
<#
    //javascript code here 
    var className = "HelloWorld";
#>

public class <#=className#>{

}
```
Will generate
```


public class HelloWorld{

}
```
### MessageBox
```java
<#
     var JOptionPane = Java.type("javax.swing.JOptionPane");
	 var className = JOptionPane.showInputDialog("Input class name");
#>
	 
public class <#=className#>{

}
```
### Call Java APIs
```java
<#
     //any java class can be called 
     //using Java.type
     //can be used javascritp or java arrays, string or other classes
     //examples:
     
     var String = Java.type("java.lang.String");
     var ArrayList = Java.type("java.util.ArrayList");
   
     var javaStringObject = new String("my java String");
     var myJavaList = new ArrayList();

     myJavaList.add(1);
     myJavaList.add(2);
#>

javaStringObject value <#=javaStringObject#>
myJavaList value <#=myJavaList#>

javaStringObject class <#=javaStringObject.getClass()#>
myJavaList class <#=myJavaList.getClass()#>
```
### Print text
```java
print text: <#
    printText("printed text");
#>

short form: <#="printed text"#>
```

### If
```java
public class TestIf{
<#if(5 > 6){#>
    private int biggerVar;
<#}else{#>
    private int smallerVar;
<#}#>

}
```

### For
```java
public class TestFor{
<#for(var i = 0; i < 10; i++){#>
   private int var<#=i#>;
<#}#>
}
```

### Current Application Path
```java
<#=currentApplicationPath#>
```


### Template File Path
```java
Template File path: <#=templateFilePath#>
Template File path Directoy: <#=templateFileDir#>
```
If a path of template file is not provided to the template generation, 
the currentApplicationPath will be returned.

### Load External JS File
```java
<#
    load(currentApplicationPath + "/LoadJSExample.js");

    //calling function "myFunction" from LoadJSExample.js
    myFunction();
#>
```


### Load external jar
```java
<#	
	addUrlToClasspath(currentApplicationPath + "/SampleJar.jar");
	var Test =  Java.type("com.sample.Test");
	
	//call static method exec from com.sample.Test
	Test.exec();
	
	//example from https://apimeister.com/2015/06/27/add-jar-to-the-classpath-at-runtime-in-jjs.html
	function addUrlToClasspath(pathName){
		var/*java.net.URLClassLoader*/ sysloader = /*(java.net.URLClassLoader) */ java.lang.ClassLoader.getSystemClassLoader();
	  var/*java.lang.Class*/ sysclass = java.net.URLClassLoader.class;
		 var ClassArray = Java.type("java.lang.Class[]");
		 var parameters = new ClassArray(1);
		 parameters[0]= java.net.URL.class;
		 var/*java.lang.reflect.Method*/ method = sysclass.getDeclaredMethod("addURL", parameters);
		 method.setAccessible(true);
		 var ObjectArray = Java.type("java.lang.Object[]");
		 var array = new ObjectArray(1);
	  var/*java.io.File*/ f = new java.io.File(pathName);
	  if(f.isFile()){
		var/*java.net.URL*/ u = f.toURL();
		array[0]=u;
		//if(u.toString().endsWith(".jar"))
		  method.invoke(sysloader, array);
	  }else{
		var/*File[]*/ listOfFiles = f.listFiles();
		if(listOfFiles !=null)
		for (var i = 0; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile()) {
			var/*java.net.URL*/ u = listOfFiles[i].toURL();
			array[0]=u;
			method.invoke(sysloader, array);
		  }
		}
	  }
	}
#>
```

### Database connection
```java
<#
    var connection = null;
    var statement = null;
	var customers = [];

    try{
        connection = getConnection("jdbc:h2:file:///" + currentApplicationPath + "/testh2db;AUTO_SERVER=TRUE", "root", "root");
		
		statement = connection.createStatement();
		var rs = statement.executeQuery("SELECT * FROM CUSTOMER");
		
		while(rs.next()){
		   var id = rs.getString("id");
		   var name = rs.getString("name");
		   
		   customers.push({"id": id, "name": name});
		}
	}finally{
	    if(connection)
		   connection.close();
		   
		if(statement)
		   statement.close();
	}

    function getConnection(url, user, password){
		var Properties = Java.type("java.util.Properties");

		var classLoader = loadJarAtRuntime(currentApplicationPath + "/h2-1.4.192.jar");
		var driverClass = classLoader.loadClass("org.h2.Driver");
		var driver = driverClass.newInstance();
		
		var properties = new Properties();
		properties.put("user", user);
		properties.put("password", password);
		
		return driver.connect(url, properties);
    }
   
   
	function loadJarAtRuntime(jarPath){
		var File = Java.type("java.io.File");
		var URLClassLoader = Java.type("java.net.URLClassLoader");
		
		
		var file = new File(jarPath);
		
		var classLoader = new URLClassLoader([file.toURI().toURL()], java.lang.ClassLoader.getSystemClassLoader());
		
		return classLoader;
	}
#>

<#for(var i = 0; i < customers.length; i++){#>
    id: <#=customers[i].id#>, name: <#=customers[i].name#>
<#}#>
```

### Call Java APIs
```java
<#
     var JOptionPane = Java.type("javax.swing.JOptionPane");
	 var className = JOptionPane.showInputDialog("Input class name");
#>
	 
public class <#=className#>{

}
```
