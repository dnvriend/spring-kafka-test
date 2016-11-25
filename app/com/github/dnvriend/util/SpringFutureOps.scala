package com.github.dnvriend.util

import org.springframework.util.concurrent.{ListenableFuture, ListenableFutureCallback}

import scala.concurrent.{Future, Promise}

object SpringFutureOps {
  implicit class ListenableFutureImplicits[A](lf: ListenableFuture[A]) {
    implicit def asScala: Future[A] = {
      val p = Promise[A]()
      lf.addCallback(new ListenableFutureCallback[A] {
        override def onFailure(ex: Throwable): Unit = p.failure(ex)
        override def onSuccess(result: A): Unit = p.success(result)
      } )
      p.future
    }
  }
}
