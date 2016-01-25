# Smart Process API (SPA)

> The semantic process API (SPA) is a vocabulary and engine that enables 
> managing semantic descriptions of process/data-models and its instances. 
> SPA becomes the globally accepted academic standard and reference 
> implementation.
> -- <cite>InES</cite>

Coming from the original vision about the SPA, the view slightly shifted towards more generality. While the main focus for the first development phases are 
processes, the goal broadend to also support other domains. Thereby, following Use Cases are the current implementation drivers:

* Semantic Process integration over format and tool borders
* Analytics like Process Mining and 
* Process Optimization by combining usage data with process models

In other words, different *project* s combine one or more vocabularies or *schema* s to express their problem domain. Data from different sources must adhere to the schema constraints in order to be drawn from such a domain. This *pool of data* is further divided into *bucket* s. For some projects, such a distinction is not useful. In the process domain, the schemas could be BPMN or YAWL. Then, stored data must be expressed in terms of BPMN or YAWL. The following ASCII Art depicts the domain and its relationships. 

```
+------------+         +------------+
|            |         |            |
| Repository +--------->   Schema   |
|            | contains|            |
+------+-----+         +-----^------+
       |                     |
       |                     |
       |                     |
+------+-----+               |
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

The purpose of the `core` module is to capture this meta-domain in code. More
specifically, the following technical capabilities are provided:

* Codified domain model
* SPA domain service
* Persistence
* Storage abstraction
* Importers and Exporters for different file formats

*To be continued*