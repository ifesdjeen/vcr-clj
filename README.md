# Clojure VCR

This is (yet another) implementation of [VCR rubygem](https://github.com/myronmarston/vcr). As [@myronmarston](https://github.com/myronmarston)
originally described it: Record your test suite's HTTP interactions and replay them during future test runs for fast, deterministic, accurate tests.

Personally, I'm still sceptical about mocking HTTP requests due to many reasons. One of them is, of course, the fact that library is made for
integration, and mocking your APIs detaches your development / CI from the original service, which can make your tests pass, but lib itself will
be broken. Use it wisely.

## What's there?

Until now (about 1 day in development), only a rough prototype of a function that's going to wrap HTTP requests is done. But lots of things planned:

  * persist your test results to the VCR cassete (YAML file)
  * replay your tests
  * schedule cassete removal, set up logic for making cassete outdated
  * mock your HTTP calls

## License

Copyright © 2012 Alex P

Distributed under the Eclipse Public License, the same as Clojure.
