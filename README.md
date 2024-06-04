# framework_2571
## sprint1 :
 Annoter la classe controller par @controller. 
 afficher une liste de classe annoter dans la page .
## sprint2 :
Annoter les methodes dans la classe controller par @get("exemple").
afficher l'url , le nom de la classe et le nom de la methode. 
## sprint3 :
Invoker la methode annoter par @get.
la methode doit retourner un String.
afficher le String dans la page. 
## sprint4 :
Cree une Classe ModelView 
    String url => Url de destination apres l'execution de la methode
    Hashmap(String,Object) data => String : nom de la variable , Object : sa valeur . data : donnee a envoyer vers cette view .
    cree la methode addObject pour mettre les donnees dans hashmap .
    cree la methode invokedMethod dans la classe Utils .
    Utiliser setAttribute dans la methode processRequest 