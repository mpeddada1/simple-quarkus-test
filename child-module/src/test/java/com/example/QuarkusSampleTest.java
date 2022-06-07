package com.example;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class QuarkusSampleTest {

  @Test
  public void testApplicationStartup() {
    System.out.println("Hello");
  }

}
