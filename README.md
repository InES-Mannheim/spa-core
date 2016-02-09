---
layout: post
title: 'SPA'
state: startpage
---
# Smart Process API (SPA)

> The semantic process API (SPA) is a vocabulary and engine that enables 
> managing semantic descriptions of process/data-models and its instances. 
> SPA becomes the globally accepted academic standard and reference 
> implementation.
> -- <cite>InES</cite>

Coming from the original vision about the SPA, the view slightly shifted towards more generality. While the main focus for the first development phases are processes, the goal broadend to also support other domains. Thereby, three Use Cases are the current implementation drivers; namely **Semantic Process integration over format and tool borders**, **Analytics like Process Mining** and **Process Optimization by combining usage data with process models**.

## Semantic Process Integration

Different tools may have different means to express processes. For example, they might differ in formats and interfaces. From a user perspective this is a rather unsatisfying situation, because processes may span over several systems implying additional manual orchastration effort. The SPA taggles this problem by consuming different interfaces and formats. Thereby, a mapping between the concepts of two formats is provided and, thus, the formats are integrated (cf. ASCII art below).

```
+------------+         +------------+          +------------+
|            |         |            |          |            |
|  Format A  +--------->  Format B  |+--------->  Format C  |
|            | Mapping |            |  Mapping |            |
+------------+         +------------+          +------------+
```

Due to the transitive nature of the mappings, data in *Format A* is instantly available in terms of *Format B* .

## Domain

Different *project* s combine one or more vocabularies or *schema* s to express their problem domain. Data from different sources must adhere to the schema constraints in order to be drawn from such a domain. This *pool of data* is further divided into *bucket* s. For some projects, such a distinction is not useful. In the process domain, the schemas could be *BPMN* or *YAWL* . Then, stored data must be expressed in terms of *BPMN* or *YAWL* . The following ASCII Art depicts the domain and its relationships. 

```
+------------+         +------------+
|            |         |            |
| Repository +--------->   Schema   |
|            | contains|            |
+------+-----+         +-----^------+
       |                     |
       |                     |
       |                     |
+------v-----+               |
|            |   links       |
|   Project  +---------------+
|            |
+------+-----+
       |
       |contains
       |
+------v-----+
|            |
| Data pool  |
|            |
+------+-----+
       |
       |consists of
       |
+------v-----+
|            |
|Data bucket |
|            |
+------------+
```

# Core

The `core` module implements the domain and provides means for alteration. The package structure is depicted below.

```
core
├── application
├── domain
│   └── model
├── io
│   └── file
│       └── xes
├── persistence
└── storage
        └── jena
```

Package `domain.model` is the bounded context and contains the codified domain model. `SPA` as the sole application service is contained in the `application` package. Further, a `SpaBuilder` is available which implements a DSL for configuring `SPA` instances. For example, `SpaBuilder.local().memory().shared()` creates an instance of the `SPA` which stores its data in a runtime shared memory location. `SPA` itself provides CRUD functionalities for the domain.

Package `io` contains the `Importer` and `Exporter` interfaces for importing data from and to Jena's `Model` inteface and, thus, RDF. Further, classes `ImporterSupport` and `ExporterSupport` are containers responsible for managing groups of `Importer`s and `Exporter`s. All file-based implementations are stored in subpackage `file`.

Package `persistence` contains all implementations responsible for mapping Java instances to RDF and vice-versa. All `Repository` implementations rely on the `Transformation` class which implements a DSL for RDF <-> Java transformations.

A leaky abstraction over `Jena` is the content of the storage package. When a `Store` supports transactions, each connection is automatically run in a transaction conext.

*To be continued*
