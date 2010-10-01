package ru.circumflex

/*!# The `core` Package

Package `core` contains different shortcuts, utilities and implicits.
You should import this package if you intend to use Circumflex API:

    import ru.circumflex.core._
*/
package object core {

  /**
   * Returns a logger instance used by Circumflex core components.
   */
  val CX_LOG = new Logger("ru.circumflex.core")

  /**
   * A shortcut for accessing the `Circumflex` singleton.
   */
  val cx = Circumflex

  /**
   * A shortcut for accessing current context.
   */
  def ctx = Context.get

  /**
   * Global messages resolver configurable via `cx.messages` configuration parameter.
   */
  lazy val msg = cx.instantiate[MessageResolver]("cx.messages", DefaultMessageResolver)

  /* Utils */

  /**
   * Converts `ThisTypeOfIdentifiers` into `that_type_of_identifiers`.
   */
  def camelCaseToUnderscore(arg: String) = arg.replaceAll("(?<!^)([A-Z])","_$1").toLowerCase

  /**
   * Executes specified `block` and counts it's execution time.
   */
  def time[T](block: => T): (Long, T) = {
    val startTime = System.currentTimeMillis
    val result = block
    (System.currentTimeMillis - startTime, result)
  }

  /**
   * Wraps an `obj` into `Some` as long as it is not `null`; returns `None` otherwise.
   */
  def any2option[T](obj: T): Option[T] = if (obj == null) None else Some(obj)

  /* Implicits */

  @inline implicit def symbol2string(sym: Symbol): String = sym.name
  @inline implicit def symbol2contextVarHelper(sym: Symbol): ContextVarHelper =
    new ContextVarHelper(sym)

}
