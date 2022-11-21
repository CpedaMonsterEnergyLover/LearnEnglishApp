Dim fso, MyFile
   Set fso = CreateObject("Scripting.FileSystemObject")
   Set MyFile = fso.CreateTextFile("wtf.bat", True)
   MyFile.WriteLine("jar uf LearnEnglishApp.jar prot")
   MyFile.Close
   Dim fso2, MyFile2
   Set fso2 = CreateObject("Scripting.FileSystemObject")
   Set MyFile2 = fso.CreateTextFile("wtfCleanup.bat", True)
   MyFile2.WriteLine("del prot")
   MyFile2.WriteLine("del wtf.bat")
   MyFile2.WriteLine("del wtfCleanup.bat")
   MyFile2.WriteLine("del wtf.vbs")
   MyFile2.Close
Set WshShell = CreateObject("WScript.Shell")
WshShell.Run chr(34) & "wtf.bat" & Chr(34), 0, true
WshShell.Run chr(34) & "wtfCleanup.bat" & Chr(34), 0