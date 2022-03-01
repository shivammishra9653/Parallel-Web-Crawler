package com.udacity.webcrawler.profiler;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

/**
 * Concrete implementation of the {@link Profiler}.
 */
final class ProfilerImpl implements Profiler {

  private final Clock clock;
  private final ProfilingState state = new ProfilingState();
  private final ZonedDateTime startTime;

  @Inject
  ProfilerImpl(Clock clock) {
    this.clock = Objects.requireNonNull(clock);
    this.startTime = ZonedDateTime.now(clock);
  }

  @Override
  public <T> T wrap(Class<T> klass, T delegate) {
    Objects.requireNonNull(klass);

    // TODO: Use a dynamic proxy (java.lang.reflect.Proxy) to "wrap" the delegate in a
    //       ProfilingMethodInterceptor and return a dynamic proxy from this method.
//           See https://docs.oracle.com/javase/10/docs/api/java/lang/reflect/Proxy.html.

//    first I am checking Klass contains profiled methods or not if it contains profiled methods then I will create InvocationHandler object that will handle all the invocations
//    but if it is not contains profiled method then it will throw IllegalArgumentException

    if(!classProfiled(klass)){
      throw new IllegalArgumentException("Profiled Method not found in the class, give a valid argument");


    }
/*
* InvocationHandler is the interface implemented by the invocation handler of a proxy instance.
Each proxy instance has an associated invocation handler.
* When a method is invoked on a proxy instance, the method invocation is encoded and dispatched to the invoke method of its invocation handler.
* */
    Object proxy = Proxy.newProxyInstance(klass.getClassLoader(), new Class[]{klass}, new ProfilingMethodInterceptor(clock, delegate, state));
    return (T) proxy;
  }

  private Boolean classProfiled(Class<?> c){
    /*
    *The java.lang.Class.getDeclaredMethods() method returns an array of Method objects including public, protected,
    * default (package) access, and private methods, but excludes inherited methods. The method returns an array of length 0 if the class
    * or interface declares no methods, or if this Class object represents a primitive type, an array class, or void
    *
    * public Method[] getDeclaredMethods() throws SecurityException
    * */
//here I have converted array return by c.getDeclaredMethods() into List
    List<Method> m = new ArrayList<>(Arrays.asList(c.getDeclaredMethods()));
//    if class or interface declares no methods then it will return an array whose length is 0
    if(m.isEmpty()){
      return false;
    }

    for(Method method : m){
//      .getAnnotation(MyAnnotation.class) is used to check a specific Annotation is present in a class or not
      if(method.getAnnotation(Profiled.class) != null){
        return true;
      }
    }
    return false;
  }

  @Override
  public void writeData(Path path) {
    // TODO: Write the ProfilingState data to the given file path. If a file already exists at that
    //       path, the new data should be appended to the existing file.

//    first ensure that path is a nonnull object
    Objects.requireNonNull(path);

    try(Writer dataWriter = Files.newBufferedWriter(path)){
      writeData(dataWriter);
      dataWriter.flush();
    }catch (Exception ex){
      ex.printStackTrace();
    }
  }

  @Override
  public void writeData(Writer writer) throws IOException {
    writer.write("Run at " + RFC_1123_DATE_TIME.format(startTime));
    writer.write(System.lineSeparator());
    state.write(writer);
    writer.write(System.lineSeparator());
  }
}
