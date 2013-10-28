shobai-dogu
===========

Tools of the trade


```java
TopologyBuilder builder;
Processor sourceOne = new SourceProcessor();
builder.addProcessor(sourceOne);
Stream streamOne = builder.createStream(sourceOne);

Processor sourceTwo = new SourceProcessor();
builder.addProcessor(sourceTwo);
Stream streamTwo = builder.createStream(sourceTwo);
String key = "record_id";

Processor join = new JoinProcessor();
builder.addProcessor(join).connectInputShuffle(streamOne).connectInputKey(streamTwo, key);
```


