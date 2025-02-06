# [WIP] Search bar extended

Search bar with additional features such as calculator, unit converter, currency converter, internet search and so on. With support for AIDL bound-services based plugins and dynamic sorting of output results.

There is a list of some core plugins like app searcher and unit&currency converter powered by [calkt](https://github.com/y9san9/calkt).

There is also a [library](https://github.com/demndevel/gester-core) that allows third party devs to create their own plugins via predefined AIDL contracts.

Stack:

- Plugins: Bound Services + AIDL
- UI: Jetpack Compose
- Data: Room
- DI: Koin
