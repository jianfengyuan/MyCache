# Cache
## Data structure
### CacheManager
> `dataMap`- dataMap is a concurrent LRU cache, to increase the `get` efficiency.
> 
> `timeMap`- timeMap is a ConcurrentHashMap, to save the expireTime of all keys.

### FileWriterReader
> `indexMap` - indexMap is a ConcurrentHashMap, to save the data paths of all entries.

## Main process

> `Store` - When the entry comes, the dataMap will cache the entry and 
> one `PUT` Event will be created and sent to EventExecutor.
> 
> `Get` - If the key in `dataMap`, the result would be returned directly. 
> If the key not in `dataMap`, it can be read from disk.
> 
> `Expire` - The main strategy is `Lazy Delete` and `Timed Delete`. 
> `Lazy Delete` means when get an expired entry, the entry will be removed
> from `dataMap` and disk directly. `Timed Delete` means there is a timer task 
> in the backend, to collect and remove expired entries.
