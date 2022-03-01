package com.udacity.webcrawler.profiler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * A method interceptor that checks whether {@link Method}s are annotated with the {@link Profiled}
 * annotation. If they are, the method interceptor records how long the method invocation took.
 */
final class ProfilingMethodInterceptor implements InvocationHandler {

  private final Clock clock;
  private final Object delegate;
  private final ProfilingState profilingState;

  // TODO: You will need to add more instance fields and constructor arguments to this class.
  ProfilingMethodInterceptor(Clock clock, Object delegate, ProfilingState profilingState) {
    this.clock = Objects.requireNonNull(clock);
    this.delegate = delegate;
    this.profilingState = profilingState;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) {
    // TODO: This method interceptor should inspect the called method to see if it is a profiled
    //       method. For profiled methods, the interceptor should record the start time, then
    //       invoke the method using the object that is being profiled. Finally, for profiled
    //       methods, the interceptor should record how long the method call took, using the
    //       ProfilingState methods.

    Instant startTime = clock.instant();
    boolean profiled = isProfiled(method);
    Object objectInvoked = null;

    try{
      objectInvoked = method.invoke(delegate, args);
    }catch (Exception ex){
      ex.printStackTrace();
    }finally{
      if(profiled){
        Duration timeDuration = Duration.between(startTime, clock.instant());
        profilingState.record(delegate.getClass(), method, timeDuration);
      }
    }
    return objectInvoked;
  }

  private boolean isProfiled(Method method){
    if(method.getAnnotation(Profiled.class) != null){
      return true;
    }

    return false;
  }
}
