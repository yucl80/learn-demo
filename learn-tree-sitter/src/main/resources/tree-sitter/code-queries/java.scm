(class_declaration
  name: (identifier) @name.definition.class) @definition.class

(method_declaration
  name: (identifier) @name.definition.method) @definition.method

(interface_declaration
  name: (identifier) @name.definition.interface) @definition.interface

(method_invocation
  name: (identifier) @name
  arguments: (argument_list) @reference.call)


(type_list
  (type_identifier) @name) @reference.implementation


(object_creation_expression
  type: (type_identifier) @name) @reference.class


(superclass (type_identifier) @name) @reference.class
