package org.superbiz.moviefun;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.albums.AlbumFixtures;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesBean;

import java.util.Map;

@Controller
public class HomeController {

    private final MoviesBean moviesBean;
    private final AlbumsBean albumsBean;
    private final MovieFixtures movieFixtures;
    private final AlbumFixtures albumFixtures;

    private TransactionTemplate transactionTemplateMovies;

    private TransactionTemplate transactionTemplateAlbums;

    public HomeController(MoviesBean moviesBean, AlbumsBean albumsBean, MovieFixtures movieFixtures, AlbumFixtures albumFixtures,
                          @Autowired PlatformTransactionManager platformTransactionManagerMovies, @Autowired PlatformTransactionManager platformTransactionManagerAlbums) {
        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;
        this.transactionTemplateMovies = new TransactionTemplate(platformTransactionManagerMovies);
        this.transactionTemplateAlbums = new TransactionTemplate(platformTransactionManagerAlbums);
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {

        transactionTemplateAlbums.execute(new TransactionCallback<Void>() {
            // the code in this method executes in a transactional context
            public Void doInTransaction(TransactionStatus status) {
                for (Album album : albumFixtures.load()) {
                    albumsBean.addAlbum(album);
                }
                return null;
            }
        });

        transactionTemplateMovies.execute(new TransactionCallback<Void>() {
            // the code in this method executes in a transactional context
            public Void doInTransaction(TransactionStatus status) {
                for (Movie movie : movieFixtures.load()) {
                    moviesBean.addMovie(movie);
                }
                return null;
            }
        });

        model.put("movies", moviesBean.getMovies());
        model.put("albums", albumsBean.getAlbums());

        return "setup";
    }

}
