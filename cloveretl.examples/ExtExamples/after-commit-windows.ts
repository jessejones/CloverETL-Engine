<?xml version="1.0" encoding="windows-1250"?>
<!DOCTYPE TestScenario SYSTEM "testscenario.dtd">
<TestScenario ident="after-commit-windows" description="After commit for Windows ext examples" useJMX="true">
	<GlobalRegEx ident="exception" expression="java.lang.Exception" caseSensitive="false" occurences="0" />
	<GlobalRegEx ident="error" expression="^ERROR" caseSensitive="false" occurences="0" />

   <FunctionalTest ident="MsSqlDataWriter" graphFile="graph/graphMsSqlDataWriter.grf">
<!-- TODO flat file analysis -->
    </FunctionalTest>
   

</TestScenario>