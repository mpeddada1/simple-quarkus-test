# simple-quarkus-test

Call the following:

1) `mvn clean install -DskipTests`
2) `cd child-module`
3) `mvn package -native`

See:
```
Version info: 'GraalVM 22.1.0 Java 17 CE'
 C compiler: gcc (linux, x86_64, 11.2.0)
 Garbage collector: Serial GC
 9 user-provided feature(s)
  - com.example.DisableLoggingAutoFeature
  - com.google.api.gax.grpc.nativeimage.GrpcNettyFeature
  - com.google.api.gax.grpc.nativeimage.ProtobufMessageFeature
  - com.google.api.gax.nativeimage.GoogleJsonClientFeature
  - com.google.api.gax.nativeimage.OpenCensusFeature
  - com.oracle.svm.thirdparty.gson.GsonFeature
  - io.quarkus.runner.AutoFeature
  - io.quarkus.runtime.graal.DisableLoggingAutoFeature
  - io.quarkus.runtime.graal.ResourcesFeature
Warning: RecomputeFieldValue.FieldOffset automatic substitution failed. The automatic substitution registration was attempted because a call to sun.misc.Unsafe.objectFieldOffset(Field) was detected in the static initializer of com.google.protobuf.UnsafeUtil. Detailed failure reason(s): The argument of Unsafe.objectFieldOffset(Field) is not a constant field., Could not determine the field where the value produced by the call to sun.misc.Unsafe.objectFieldOffset(Field) for the field offset computation is stored. The call is not directly followed by a field store or by a sign extend node followed directly by a field store. 
[2/7] Performing analysis...  [*]                                                                        (6.8s @ 0.73GB)
   4,660 (76.89%) of  6,061 classes reachable
   6,125 (56.34%) of 10,872 fields reachable
  24,176 (43.62%) of 55,424 methods reachable
     389 classes,   945 fields, and 6,145 methods registered for reflection

Fatal error: org.graalvm.compiler.debug.GraalError: com.oracle.graal.pointsto.constraints.UnsupportedFeatureException: No instances of io.grpc.netty.shaded.io.netty.util.internal.logging.Slf4JLoggerFactory are allowed in the image heap as this class should be initialized at image runtime. Object has been initialized by the io.grpc.netty.shaded.io.netty.channel.AbstractChannel class initializer with a trace: 
 	at io.grpc.netty.shaded.io.netty.util.internal.logging.Slf4JLoggerFactory.<init>(Slf4JLoggerFactory.java:42)
	at io.grpc.netty.shaded.io.netty.util.internal.logging.Slf4JLoggerFactory.<clinit>(Slf4JLoggerFactory.java:33)
	at io.grpc.netty.shaded.io.netty.util.internal.logging.InternalLoggerFactory.useSlf4JLoggerFactory(InternalLoggerFactory.java:62)
	at io.grpc.netty.shaded.io.netty.util.internal.logging.InternalLoggerFactory.newDefaultFactory(InternalLoggerFactory.java:42)
	at io.grpc.netty.shaded.io.netty.util.internal.logging.InternalLoggerFactory.getDefaultFactory(InternalLoggerFactory.java:111)
	at io.grpc.netty.shaded.io.netty.util.internal.logging.InternalLoggerFactory.getInstance(InternalLoggerFactory.java:134)
	at io.grpc.netty.shaded.io.netty.util.internal.logging.InternalLoggerFactory.getInstance(InternalLoggerFactory.java:127)
	at io.grpc.netty.shaded.io.netty.channel.AbstractChannel.<clinit>(AbstractChannel.java:45)
.  To fix the issue mark io.grpc.netty.shaded.io.netty.util.internal.logging.Slf4JLoggerFactory for build-time initialization with --initialize-at-build-time=io.grpc.netty.shaded.io.netty.util.internal.logging.Slf4JLoggerFactory or use the the information from the trace to find the culprit and --initialize-at-run-time=<culprit> to prevent its instantiation.

	at com.oracle.graal.pointsto.util.AnalysisFuture.setException(AnalysisFuture.java:49)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:269)
	at com.oracle.graal.pointsto.util.AnalysisFuture.ensureDone(AnalysisFuture.java:63)
	at com.oracle.graal.pointsto.heap.ImageHeapScanner.lambda$postTask$9(ImageHeapScanner.java:611)
	at com.oracle.graal.pointsto.util.CompletionExecutor.executeCommand(CompletionExecutor.java:193)
	at com.oracle.graal.pointsto.util.CompletionExecutor.lambda$executeService$0(CompletionExecutor.java:177)
	at java.base/java.util.concurrent.ForkJoinTask$RunnableExecuteAction.exec(ForkJoinTask.java:1395)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:373)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1182)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1655)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1622)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:165)

```

We are explicitly initializing the slf4jLoggerFactory at run-time in https://github.com/googleapis/gax-java/blob/0f0996c7c4bea2af3a01979fd3e21cfb6a96d565/gax-grpc/src/main/resources/META-INF/native-image/com.google.api/gax-grpc/native-image.properties#L6 but quarkus seems to be causing it to be initalized at build-time.
