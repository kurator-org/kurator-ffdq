# BDQ SHACL shapes (test resource)

`bdqffdq_SHACL_constraints.ttl` is a copy of
[`tg2/core/bdqffdq_SHACL_constraints.ttl`](https://github.com/tdwg/bdq/blob/3350a4ea/tg2/core/bdqffdq_SHACL_constraints.ttl)
from [tdwg/bdq](https://github.com/tdwg/bdq) at commit `3350a4ea` (2026-11-09): the SHACL shapes of the
BDQ standard's Fitness For Use Framework ontology (bdqffdq).

`BdqffdqShaclTest` validates the report RDF that kurator-ffdq writes against these shapes. BDQ is the
source of truth: when the shapes change in tdwg/bdq, replace this copy, and change kurator-ffdq rather
than the shapes.
